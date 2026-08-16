package mk.ukim.finki.searchindexing.indexing.core;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mk.ukim.finki.searchindexing.config.CrawlerProperties;
import mk.ukim.finki.searchindexing.indexing.crawler.FetchedPage;
import mk.ukim.finki.searchindexing.indexing.crawler.WebCrawler;
import mk.ukim.finki.searchindexing.indexing.parser.DocumentParser;
import mk.ukim.finki.searchindexing.indexing.parser.LanguageDetector;
import mk.ukim.finki.searchindexing.indexing.parser.ParsedDocument;
import mk.ukim.finki.searchindexing.indexing.search.IndexDocument;
import mk.ukim.finki.searchindexing.indexing.search.SearchIndex;
import mk.ukim.finki.searchindexing.model.domain.CrawlSeed;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexedDocumentDto;
import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;

/**
 * The generic crawl-index loop, shared by every pipeline implementation.
 *
 * <p>The loop is intentionally {@code final}: the assignment is NOT to change
 * how the frontier is traversed, but to implement the seams it is built from
 * ({@link WebCrawler}, {@link DocumentParser}, {@link LanguageDetector},
 * {@link SearchIndex}) and the website-specific hooks ({@link #docIdFor(String)},
 * {@link #shouldFollow(String, IndexingJob)} and optionally
 * {@link #normalizeUrl(String)}).</p>
 *
 * <p>For each URL, starting from the job's seeds: FETCH the page, PARSE it,
 * DETECT its Macedonian-language confidence, INDEX it, then ENQUEUE the
 * discovered links that pass {@link #shouldFollow(String, IndexingJob)}. The
 * run is bounded by {@code crawler.max-pages-per-job} and paced by
 * {@code crawler.request-delay-ms}.</p>
 */
public abstract class AbstractCrawlIndexPipeline implements CrawlIndexPipeline {
    private static final int DEFAULT_MAX_PAGES = 100;

    protected final WebCrawler webCrawler;
    protected final DocumentParser documentParser;
    protected final LanguageDetector languageDetector;
    protected final SearchIndex searchIndex;
    protected final CrawlerProperties crawlerProperties;

    protected AbstractCrawlIndexPipeline(
        WebCrawler webCrawler,
        DocumentParser documentParser,
        LanguageDetector languageDetector,
        SearchIndex searchIndex,
        CrawlerProperties crawlerProperties
    ) {
        this.webCrawler = webCrawler;
        this.documentParser = documentParser;
        this.languageDetector = languageDetector;
        this.searchIndex = searchIndex;
        this.crawlerProperties = crawlerProperties;
    }

    /**
     * Derives a stable identifier for a document in the search index from its
     * URL (e.g. the URL itself, or a hash of it). Adding two documents with the
     * same id replaces the first — this is how re-crawls stay idempotent.
     */
    protected abstract String docIdFor(String url);

    /**
     * Decides whether a discovered link should be added to the crawl frontier,
     * e.g. only links that stay on the job's {@code baseUrl} host and point at
     * content worth indexing.
     */
    protected abstract boolean shouldFollow(String url, IndexingJob job);

    /**
     * Normalises a URL before it is de-duplicated and enqueued (e.g. strip the
     * fragment, resolve relative links, drop tracking query params). The default
     * returns the URL unchanged; override it for your website when needed.
     */
    protected String normalizeUrl(String url) {
        return url;
    }

    @Override
    public final List<CreateIndexedDocumentDto> execute(
        IndexingJob job,
        List<CrawlSeed> seeds,
        CrawlStepListener stepListener
    ) {
        List<CreateIndexedDocumentDto> collected = new ArrayList<>();
        Deque<String> frontier = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();

        for (CrawlSeed seed : seeds) {
            String start = normalizeUrl(seed.getValue());
            if (seen.add(start)) {
                frontier.add(start);
            }
        }

        int maxPages = crawlerProperties.maxPagesPerJob() == null
            ? DEFAULT_MAX_PAGES
            : crawlerProperties.maxPagesPerJob();

        while (!frontier.isEmpty() && collected.size() < maxPages) {
            String url = frontier.poll();

            FetchedPage page;
            try {
                page = webCrawler.fetch(url);
            } catch (RuntimeException exception) {
                stepListener.onStep(CrawlActionType.FETCH, url + " -> " + exception.getMessage(), false);
                continue;
            }
            if (page == null || !page.isOk()) {
                int status = page == null ? -1 : page.statusCode();
                stepListener.onStep(CrawlActionType.SKIP, url + " -> status " + status, false);
                continue;
            }
            stepListener.onStep(CrawlActionType.FETCH, url + " -> " + page.statusCode(), true);

            ParsedDocument parsed;
            try {
                parsed = documentParser.parse(page);
            } catch (RuntimeException exception) {
                stepListener.onStep(CrawlActionType.PARSE, url + " -> " + exception.getMessage(), false);
                continue;
            }
            stepListener.onStep(CrawlActionType.PARSE, "\"" + parsed.title() + "\"", true);

            double confidence = parsed.textContent() == null
                ? 0.0
                : languageDetector.macedonianConfidence(parsed.textContent());
            stepListener.onStep(CrawlActionType.DETECT_LANGUAGE, "macedonianConfidence=" + confidence, true);

            String docId = docIdFor(url);
            CreateIndexedDocumentDto document = new CreateIndexedDocumentDto(
                docId,
                url,
                parsed.title(),
                parsed.textContent(),
                parsed.resourceType(),
                LocalDateTime.now(),
                confidence,
                parsed.mediaItems()
            );

            try {
                searchIndex.index(new IndexDocument(docId, url, parsed.title(), parsed.textContent(), confidence));
                collected.add(document);
                stepListener.onStep(CrawlActionType.INDEX, "indexed " + url, true);
            } catch (RuntimeException exception) {
                stepListener.onStep(CrawlActionType.INDEX, url + " -> " + exception.getMessage(), false);
            }

            enqueueLinks(job, parsed, seen, frontier, stepListener);
            politenessDelay();
        }

        stepListener.onStep(CrawlActionType.FINISH, "indexed " + collected.size() + " document(s)", true);
        return collected;
    }

    private void enqueueLinks(
        IndexingJob job,
        ParsedDocument parsed,
        Set<String> seen,
        Deque<String> frontier,
        CrawlStepListener stepListener
    ) {
        if (parsed.discoveredLinks() == null) {
            return;
        }
        for (String link : parsed.discoveredLinks()) {
            String normalized = normalizeUrl(link);
            if (seen.contains(normalized) || !shouldFollow(normalized, job)) {
                continue;
            }
            seen.add(normalized);
            frontier.add(normalized);
            stepListener.onStep(CrawlActionType.ENQUEUE, normalized, true);
        }
    }

    private void politenessDelay() {
        Long delay = crawlerProperties.requestDelayMs();
        if (delay == null || delay <= 0) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void shutdown() {
        // No resources to release by default; override when your WebCrawler
        // holds an HTTP client / browser that must be closed.
    }
}
