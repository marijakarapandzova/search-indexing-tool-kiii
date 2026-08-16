package mk.ukim.finki.searchindexing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the crawl-index pipeline, bound from the {@code crawler.*}
 * properties.
 *
 * @param maxPagesPerJob hard upper bound on how many pages a single
 *                       {@code IndexingJob} may fetch and index before stopping
 * @param sameDomainOnly whether the pipeline should only follow links that stay
 *                       on the job's {@code baseUrl} host
 * @param requestDelayMs politeness delay (milliseconds) to wait between fetches
 * @param userAgent      the {@code User-Agent} the {@code WebCrawler} should send
 */
@ConfigurationProperties(prefix = "crawler")
public record CrawlerProperties(
    Integer maxPagesPerJob,
    Boolean sameDomainOnly,
    Long requestDelayMs,
    String userAgent
) {
}
