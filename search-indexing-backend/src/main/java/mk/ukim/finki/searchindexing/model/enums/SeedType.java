package mk.ukim.finki.searchindexing.model.enums;

/**
 * What a {@code CrawlSeed} points at within the assigned website — the entry
 * point(s) the crawl-index pipeline starts from.
 */
public enum SeedType {
    URL,
    SITEMAP,
    SECTION
}
