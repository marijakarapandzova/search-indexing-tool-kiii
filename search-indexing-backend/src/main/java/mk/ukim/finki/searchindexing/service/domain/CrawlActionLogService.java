package mk.ukim.finki.searchindexing.service.domain;

import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlActionLog;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;

/**
 * Persists and reads the trace of the crawl-index pipeline.
 * Fully provided — this is infrastructure for observing your job runs, not
 * part of the assignment.
 */
public interface CrawlActionLogService {
    CrawlActionLog log(IndexingJob job, CrawlActionType actionType, String details, boolean successful);

    List<CrawlActionLog> findByJobId(Long jobId);
}
