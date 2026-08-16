package mk.ukim.finki.searchindexing.indexing.core;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexedDocumentDto;
import mk.ukim.finki.searchindexing.model.exception.JobNotFoundException;
import mk.ukim.finki.searchindexing.service.domain.CrawlActionLogService;
import mk.ukim.finki.searchindexing.service.domain.IndexedDocumentService;
import mk.ukim.finki.searchindexing.service.domain.IndexingJobService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IndexingOrchestratorImpl implements IndexingOrchestrator {
    private final CrawlIndexPipeline crawlIndexPipeline;
    private final IndexingJobService indexingJobService;
    private final IndexedDocumentService indexedDocumentService;
    private final CrawlActionLogService crawlActionLogService;

    public IndexingOrchestratorImpl(
        CrawlIndexPipeline crawlIndexPipeline,
        IndexingJobService indexingJobService,
        IndexedDocumentService indexedDocumentService,
        CrawlActionLogService crawlActionLogService
    ) {
        this.crawlIndexPipeline = crawlIndexPipeline;
        this.indexingJobService = indexingJobService;
        this.indexedDocumentService = indexedDocumentService;
        this.crawlActionLogService = crawlActionLogService;
    }

    /**
     * Runs the whole crawl-index pipeline inside one transaction. This keeps
     * {@code job.getSeeds()} (a lazy collection) loadable from this
     * background thread and every {@code CrawlActionLog} write consistent
     * with the job's final status; the tradeoff is a long-lived DB
     * connection for the duration of the crawl, which is acceptable given
     * {@code crawler.max-pages-per-job} bounds how long a run can take.
     */
    @Override
    @Transactional
    public void runJob(Long jobId) {
        IndexingJob job = indexingJobService
            .findById(jobId)
            .orElseThrow(() -> new JobNotFoundException(jobId));

        try {
            List<CreateIndexedDocumentDto> documents = crawlIndexPipeline.execute(
                job,
                job.getSeeds(),
                (type, details, successful) -> crawlActionLogService.log(job, type, details, successful)
            );

            List<IndexedDocument> entities = documents
                .stream()
                .map(dto -> dto.toIndexedDocument(job))
                .toList();
            indexedDocumentService.saveAll(entities);

            indexingJobService.complete(jobId);
            log.info("Indexing job {} completed with {} document(s).", jobId, entities.size());
        } catch (RuntimeException exception) {
            log.error("Indexing job {} failed: {}", jobId, exception.getMessage(), exception);
            indexingJobService.fail(jobId);
        } finally {
            crawlIndexPipeline.shutdown();
        }
    }
}
