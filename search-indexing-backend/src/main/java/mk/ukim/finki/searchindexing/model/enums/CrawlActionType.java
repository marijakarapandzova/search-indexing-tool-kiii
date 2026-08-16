package mk.ukim.finki.searchindexing.model.enums;

/**
 * The primitive steps the crawl-index pipeline performs for each URL it visits.
 * The {@code AbstractCrawlIndexPipeline} loop emits one of these per step and
 * reports it through the {@code CrawlStepListener}, so the frontend can show a
 * live trace of the job.
 */
public enum CrawlActionType {
    FETCH,
    PARSE,
    DETECT_LANGUAGE,
    INDEX,
    ENQUEUE,
    SKIP,
    FINISH
}
