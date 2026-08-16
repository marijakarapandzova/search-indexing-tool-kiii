package mk.ukim.finki.searchindexing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.transaction.Transactional;
import mk.ukim.finki.searchindexing.integration.vezilka.DonationReceipt;
import mk.ukim.finki.searchindexing.integration.vezilka.VezilkaClient;
import mk.ukim.finki.searchindexing.model.domain.DonationBatch;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.dto.CreateDonationBatchDto;
import mk.ukim.finki.searchindexing.model.enums.DonationStatus;
import mk.ukim.finki.searchindexing.model.enums.JobStatus;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;
import mk.ukim.finki.searchindexing.repository.DonationBatchRepository;
import mk.ukim.finki.searchindexing.repository.IndexedDocumentRepository;
import mk.ukim.finki.searchindexing.repository.IndexingJobRepository;
import mk.ukim.finki.searchindexing.service.application.DonationApplicationService;
import mk.ukim.finki.searchindexing.service.domain.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Testcontainers
@Transactional
public class DonationServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("searchindex_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DonationApplicationService donationApplicationService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private IndexingJobRepository indexingJobRepository;

    @Autowired
    private IndexedDocumentRepository indexedDocumentRepository;

    @Autowired
    private DonationBatchRepository donationBatchRepository;

    @MockBean
    private VezilkaClient vezilkaClient;

    @BeforeEach
    void setUp() {
        // Create an indexing job
        IndexingJob job = new IndexingJob("https://example.mk", "Test website");
        job.setStatus(JobStatus.COMPLETED);
        IndexingJob savedJob = indexingJobRepository.save(job);

        // Create some indexed documents
        for (int i = 1; i <= 3; i++) {
            IndexedDocument doc = new IndexedDocument();
            doc.setDocId("doc" + i);
            doc.setUrl("https://example.mk/page" + i);
            doc.setTitle("Page " + i);
            doc.setContent("Content for page " + i);
            doc.setResourceType(ResourceType.PAGE);
            doc.setMacedonianConfidence(0.9);
            doc.setJob(savedJob);
            indexedDocumentRepository.save(doc);
        }
    }

    @Test
    void testCreateBatch() {
        var documents = indexedDocumentRepository.findAll();
        List<Long> documentIds = documents.stream()
            .limit(2)
            .map(IndexedDocument::getId)
            .toList();

        CreateDonationBatchDto dto = new CreateDonationBatchDto(documentIds);
        var batch = donationApplicationService.create(dto);

        assertThat(batch).isNotNull();
        assertThat(batch.getStatus()).isEqualTo(DonationStatus.DRAFT);
        assertThat(batch.getDocumentIds()).hasSize(2);
    }

    @Test
    void testApproveBatch() {
        // Create a batch first
        var documents = indexedDocumentRepository.findAll();
        List<Long> documentIds = documents.stream()
            .limit(2)
            .map(IndexedDocument::getId)
            .toList();

        CreateDonationBatchDto dto = new CreateDonationBatchDto(documentIds);
        var batch = donationApplicationService.create(dto);

        // Approve it
        var approved = donationApplicationService.approve(batch.getId());

        assertThat(approved.getStatus()).isEqualTo(DonationStatus.APPROVED);
        assertThat(approved.getId()).isEqualTo(batch.getId());
    }

    @Test
    void testSubmitBatch() {
        // Create and approve a batch
        var documents = indexedDocumentRepository.findAll();
        List<Long> documentIds = documents.stream()
            .limit(2)
            .map(IndexedDocument::getId)
            .toList();

        CreateDonationBatchDto dto = new CreateDonationBatchDto(documentIds);
        var batch = donationApplicationService.create(dto);
        var approved = donationApplicationService.approve(batch.getId());

        // Mock the VezilkaClient response
        DonationReceipt receipt = new DonationReceipt("vez-12345", DonationStatus.ACCEPTED);
        when(vezilkaClient.submitDonation(
            approved.getId(),
            documents.stream().limit(2).map(IndexedDocument::getContent).toList()
        )).thenReturn(receipt);

        // Submit it
        var submitted = donationApplicationService.submit(approved.getId());

        assertThat(submitted.getStatus()).isEqualTo(DonationStatus.SUBMITTED);
        assertThat(submitted.getVezilkaReference()).isNotNull();
        assertThat(submitted.getSubmittedAt()).isNotNull();
    }

    @Test
    void testDonationWorkflow() {
        // Create documents
        var documents = indexedDocumentRepository.findAll();
        assertThat(documents).isNotEmpty();

        List<Long> documentIds = documents.stream()
            .map(IndexedDocument::getId)
            .toList();

        // Step 1: Create batch (DRAFT status)
        CreateDonationBatchDto createDto = new CreateDonationBatchDto(documentIds);
        var draftBatch = donationApplicationService.create(createDto);

        assertThat(draftBatch.getStatus()).isEqualTo(DonationStatus.DRAFT);
        assertThat(draftBatch.getDocumentIds()).hasSize(3);

        // Step 2: Approve batch (APPROVED status)
        var approvedBatch = donationApplicationService.approve(draftBatch.getId());

        assertThat(approvedBatch.getStatus()).isEqualTo(DonationStatus.APPROVED);
        assertThat(approvedBatch.getId()).isEqualTo(draftBatch.getId());

        // Step 3: Submit batch (SUBMITTED status)
        DonationReceipt receipt = new DonationReceipt("vez-ref-123", DonationStatus.ACCEPTED);
        when(vezilkaClient.submitDonation(
            approvedBatch.getId(),
            documents.stream().map(IndexedDocument::getContent).toList()
        )).thenReturn(receipt);

        var submittedBatch = donationApplicationService.submit(approvedBatch.getId());

        assertThat(submittedBatch.getStatus()).isEqualTo(DonationStatus.SUBMITTED);
        assertThat(submittedBatch.getVezilkaReference()).isEqualTo("vez-ref-123");
        assertThat(submittedBatch.getSubmittedAt()).isNotNull();

        // Step 4: Verify batch transitions through states correctly
        var foundBatch = donationBatchRepository.findById(draftBatch.getId());
        assertThat(foundBatch).isPresent();
        assertThat(foundBatch.get().getStatus()).isEqualTo(DonationStatus.SUBMITTED);
    }
}
