package mk.ukim.finki.searchindexing.service.domain.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.integration.vezilka.DonationReceipt;
import mk.ukim.finki.searchindexing.integration.vezilka.TextDonationRequest;
import mk.ukim.finki.searchindexing.integration.vezilka.VezilkaClient;
import mk.ukim.finki.searchindexing.model.domain.DonationBatch;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.enums.DonationStatus;
import mk.ukim.finki.searchindexing.model.exception.DonationBatchNotFoundException;
import mk.ukim.finki.searchindexing.model.exception.InvalidDonationStateException;
import mk.ukim.finki.searchindexing.repository.DonationBatchRepository;
import mk.ukim.finki.searchindexing.service.domain.DonationService;
import mk.ukim.finki.searchindexing.service.domain.IndexedDocumentService;
import org.springframework.stereotype.Service;

@Service
public class DonationServiceImpl implements DonationService {
    private final DonationBatchRepository donationBatchRepository;
    private final IndexedDocumentService indexedDocumentService;
    private final VezilkaClient vezilkaClient;

    public DonationServiceImpl(
        DonationBatchRepository donationBatchRepository,
        IndexedDocumentService indexedDocumentService,
        VezilkaClient vezilkaClient
    ) {
        this.donationBatchRepository = donationBatchRepository;
        this.indexedDocumentService = indexedDocumentService;
        this.vezilkaClient = vezilkaClient;
    }

    @Override
    public List<DonationBatch> findAll() {
        return donationBatchRepository.findAll();
    }

    @Override
    public Optional<DonationBatch> findById(Long id) {
        return donationBatchRepository.findById(id);
    }

    @Override
    public DonationBatch createBatch(List<Long> documentIds) {
        List<IndexedDocument> documents = indexedDocumentService.findAllById(documentIds);

        DonationBatch batch = new DonationBatch(DonationStatus.DRAFT);
        batch = donationBatchRepository.save(batch);

        DonationBatch savedBatch = batch;
        for (IndexedDocument document : documents) {
            document.setDonationBatch(savedBatch);
        }
        indexedDocumentService.saveAll(documents);
        savedBatch.getDocuments().addAll(documents);
        return savedBatch;
    }

    @Override
    public DonationBatch approve(Long id) {
        DonationBatch batch = findOrThrow(id);
        if (batch.getStatus() != DonationStatus.DRAFT) {
            throw new InvalidDonationStateException(id, batch.getStatus());
        }
        batch.setStatus(DonationStatus.APPROVED);
        return donationBatchRepository.save(batch);
    }

    @Override
    public DonationBatch submit(Long id) {
        DonationBatch batch = findOrThrow(id);
        if (batch.getStatus() != DonationStatus.APPROVED) {
            throw new InvalidDonationStateException(id, batch.getStatus());
        }

        String title = "Macedonian content from 360stepeni.mk, batch #" + batch.getId();
        StringBuilder content = new StringBuilder();
        StringBuilder sourceUrls = new StringBuilder();
        for (IndexedDocument document : batch.getDocuments()) {
            content.append(document.getTitle()).append("\n").append(document.getContent()).append("\n\n");
            sourceUrls.append(document.getUrl()).append("\n");
        }

        TextDonationRequest request = new TextDonationRequest(title, content.toString(), sourceUrls.toString());
        DonationReceipt receipt = vezilkaClient.submitTextDonation(request);

        batch.setVezilkaReference(receipt.reference());
        batch.setSubmittedAt(LocalDateTime.now());
        batch.setStatus(DonationStatus.SUBMITTED);
        return donationBatchRepository.save(batch);
    }

    @Override
    public void refreshSubmittedStatuses() {
        List<DonationBatch> submittedBatches = donationBatchRepository.findAllByStatus(DonationStatus.SUBMITTED);

        for (DonationBatch batch : submittedBatches) {
            if (batch.getVezilkaReference() == null) {
                continue;
            }
            DonationStatus status = vezilkaClient.checkStatus(batch.getVezilkaReference());
            if (status != batch.getStatus()) {
                batch.setStatus(status);
                donationBatchRepository.save(batch);
            }
        }
    }

    private DonationBatch findOrThrow(Long id) {
        return donationBatchRepository.findById(id).orElseThrow(() -> new DonationBatchNotFoundException(id));
    }
}
