package mk.ukim.finki.searchindexing.events;

/**
 * Published after an indexing job is started via the API.
 * Handled asynchronously by {@code IndexingJobStartedListener}, which kicks
 * off the crawl-index run outside of the web request.
 */
public record IndexingJobStartedEvent(Long jobId) {
}
