package mk.ukim.finki.searchindexing.indexing.crawler;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.config.CrawlerProperties;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

/**
 * Fetches a single page using JSoup's HTTP client. Sends the configured
 * {@code User-Agent} (see {@link CrawlerProperties}) and, before fetching,
 * checks the target host's {@code robots.txt} via {@link RobotsTxtChecker}
 * so the pipeline stays a polite crawler.
 *
 * <p>The politeness delay between requests is handled by the crawl-index
 * loop ({@code AbstractCrawlIndexPipeline}), not here.</p>
 */
@Component
@Slf4j
public class JsoupWebCrawler implements WebCrawler {
    private static final int TIMEOUT_MS = 15_000;
    private static final String DEFAULT_USER_AGENT = "EMC-SearchIndexingBot/1.0 (+https://finki.ukim.mk)";

    private final CrawlerProperties crawlerProperties;
    private final RobotsTxtChecker robotsTxtChecker;

    public JsoupWebCrawler(CrawlerProperties crawlerProperties, RobotsTxtChecker robotsTxtChecker) {
        this.crawlerProperties = crawlerProperties;
        this.robotsTxtChecker = robotsTxtChecker;
    }

    @Override
    public FetchedPage fetch(String url) {
        if (!robotsTxtChecker.isAllowed(url)) {
            log.debug("robots.txt disallows fetching {}", url);
            return new FetchedPage(url, 999, null, null, LocalDateTime.now());
        }

        String userAgent = crawlerProperties.userAgent() == null
            ? DEFAULT_USER_AGENT
            : crawlerProperties.userAgent();

        try {
            Connection.Response response = Jsoup
                .connect(url)
                .userAgent(userAgent)
                .timeout(TIMEOUT_MS)
                .maxBodySize(5 * 1024 * 1024)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute();

            String contentType = response.contentType();
            boolean isHtmlLike = contentType == null
                || contentType.contains("html")
                || contentType.contains("xml")
                || contentType.contains("text");

            String body = isHtmlLike ? response.body() : null;

            return new FetchedPage(url, response.statusCode(), contentType, body, LocalDateTime.now());
        } catch (Exception exception) {
            log.debug("Failed to fetch {}: {}", url, exception.getMessage());
            throw new RuntimeException("Failed to fetch " + url + ": " + exception.getMessage(), exception);
        }
    }
}
