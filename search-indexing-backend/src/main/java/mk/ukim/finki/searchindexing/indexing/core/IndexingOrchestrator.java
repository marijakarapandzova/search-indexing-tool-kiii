package mk.ukim.finki.searchindexing.indexing.core;

/**
 * Entry point for running a whole indexing job, called asynchronously by the
 * {@code IndexingJobStartedListener} after a job is started via the API.
 */
public interface IndexingOrchestrator {
    /**
     * Runs the crawl-index pipeline for the given job: executes the loop over
     * the job's seeds, persists the indexed documents and the action logs, and
     * finally marks the job COMPLETED or FAILED.
     */
    void runJob(Long jobId);
}
