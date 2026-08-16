package mk.ukim.finki.searchindexing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import mk.ukim.finki.searchindexing.config.JpaConfig;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Import(JpaConfig.class)
@Transactional
@Testcontainers
public class IndexingJobRepositoryTest {
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
    private IndexingJobRepository indexingJobRepository;

    @BeforeEach
    void setUp() {
        // Create test jobs with different statuses
        IndexingJob job1 = new IndexingJob("https://example1.mk", "First website");
        job1.setStatus(JobStatus.CREATED);
        indexingJobRepository.save(job1);

        IndexingJob job2 = new IndexingJob("https://example2.mk", "Second website");
        job2.setStatus(JobStatus.RUNNING);
        indexingJobRepository.save(job2);

        IndexingJob job3 = new IndexingJob("https://example3.mk", "Third website");
        job3.setStatus(JobStatus.COMPLETED);
        indexingJobRepository.save(job3);
    }

    @Test
    void testFindAllJobs() {
        var jobs = indexingJobRepository.findAll();
        assertThat(jobs).hasSize(3);
    }

    @Test
    void testSaveAndFindById() {
        IndexingJob job = new IndexingJob("https://test.mk", "Test job");
        job.setStatus(JobStatus.CREATED);
        IndexingJob saved = indexingJobRepository.save(job);

        var found = indexingJobRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBaseUrl()).isEqualTo("https://test.mk");
        assertThat(found.get().getStatus()).isEqualTo(JobStatus.CREATED);
    }

    @Test
    void testUpdateJobStatus() {
        var jobs = indexingJobRepository.findAll();
        IndexingJob job = jobs.stream()
            .filter(j -> j.getStatus() == JobStatus.CREATED)
            .findFirst()
            .orElseThrow();

        job.setStatus(JobStatus.RUNNING);
        indexingJobRepository.save(job);

        var updated = indexingJobRepository.findById(job.getId());
        assertThat(updated.get().getStatus()).isEqualTo(JobStatus.RUNNING);
    }

    @Test
    void testDeleteJob() {
        var jobs = indexingJobRepository.findAll();
        assertThat(jobs).hasSize(3);

        IndexingJob jobToDelete = jobs.get(0);
        indexingJobRepository.deleteById(jobToDelete.getId());

        var remaining = indexingJobRepository.findAll();
        assertThat(remaining).hasSize(2);
    }
}
