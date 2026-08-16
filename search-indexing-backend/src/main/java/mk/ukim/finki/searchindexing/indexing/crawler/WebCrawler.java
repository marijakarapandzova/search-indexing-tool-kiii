package mk.ukim.finki.searchindexing.indexing.crawler;

/**
 * The fetching seam of the pipeline: how a single URL of the assigned website
 * is downloaded.
 *
 * <p>TODO(student): Provide an implementation backed by an HTTP client / crawling
 * library of your choice (JSoup, Java's {@code HttpClient}, crawler4j, or a
 * headless browser such as Playwright/Selenium for JavaScript-heavy sites). The
 * implementation must send the configured {@code User-Agent} and respect the
 * target's {@code robots.txt} and rate limits — see {@code CrawlerProperties}.</p>
 *
 * <p>The crawl-index loop calls {@link #fetch(String)} once per URL it visits;
 * link discovery, deduplication and the politeness delay are handled by the
 * loop, not here.</p>
 */
public interface WebCrawler {
    /**
     * Fetches a single URL.
     *
     * @param url the absolute URL to download
     * @return the fetched page (check {@link FetchedPage#isOk()} before parsing)
     */
    FetchedPage fetch(String url);
}
