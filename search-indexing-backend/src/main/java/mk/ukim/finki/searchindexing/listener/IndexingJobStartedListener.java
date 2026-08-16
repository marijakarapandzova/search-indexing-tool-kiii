package mk.ukim.finki.searchindexing.listener;

import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.events.IndexingJobStartedEvent;
import mk.ukim.finki.searchindexing.indexing.core.IndexingOrchestrator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Runs the crawl-index pipeline asynchronously once a job start has been
 * committed, so the HTTP request that started the job returns immediately.
 */
@Component
@Slf4j
public class IndexingJobStartedListener {
    private final IndexingOrchestrator indexingOrchestrator;

    public IndexingJobStartedListener(IndexingOrchestrator indexingOrchestrator) {
        this.indexingOrchestrator = indexingOrchestrator;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onIndexingJobStarted(IndexingJobStartedEvent event) {
        log.info("[ASYNC - thread: {}] Running crawl-index pipeline for indexing job {}.",
            Thread.currentThread().getName(), event.jobId());
        indexingOrchestrator.runJob(event.jobId());
    }
}
