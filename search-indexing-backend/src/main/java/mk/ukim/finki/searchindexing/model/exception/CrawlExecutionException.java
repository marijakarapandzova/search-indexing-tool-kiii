package mk.ukim.finki.searchindexing.model.exception;

/**
 * Thrown by the indexing layers ({@code WebCrawler}, {@code DocumentParser},
 * {@code SearchIndex}, {@code CrawlIndexPipeline}) when a step of the
 * crawl-index pipeline fails unrecoverably.
 */
public class CrawlExecutionException extends RuntimeException {
    public CrawlExecutionException(String message) {
        super(message);
    }

    public CrawlExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
