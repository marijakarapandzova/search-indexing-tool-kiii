package mk.ukim.finki.searchindexing.indexing.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import mk.ukim.finki.searchindexing.config.CrawlerProperties;
import mk.ukim.finki.searchindexing.indexing.crawler.WebCrawler;
import mk.ukim.finki.searchindexing.indexing.parser.DocumentParser;
import mk.ukim.finki.searchindexing.indexing.parser.LanguageDetector;
import mk.ukim.finki.searchindexing.indexing.search.SearchIndex;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import org.springframework.stereotype.Component;

/**
 * The crawl-index pipeline for <a href="https://360stepeni.mk">360stepeni.mk</a>,
 * a WordPress-based Macedonian news outlet. Only the two frontier hooks are
 * implemented here; the fetch/parse/detect/index loop itself lives in
 * {@link AbstractCrawlIndexPipeline}.
 */
@Component
public class Stepeni360Pipeline extends AbstractCrawlIndexPipeline {
    private static final List<String> EXCLUDED_PATH_FRAGMENTS = List.of(
        "/wp-content/", "/wp-json/", "/wp-admin/", "/wp-login", "/feed", "/xmlrpc.php",
        "/wp-includes/", "?share=", "/attachment/"
    );

    private static final List<String> EXCLUDED_EXTENSIONS = List.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg", ".pdf", ".zip", ".rar",
        ".mp4", ".mp3", ".avi", ".mov", ".css", ".js", ".ico", ".woff", ".woff2"
    );

    public Stepeni360Pipeline(
        WebCrawler webCrawler,
        DocumentParser documentParser,
        LanguageDetector languageDetector,
        SearchIndex searchIndex,
        CrawlerProperties crawlerProperties
    ) {
        super(webCrawler, documentParser, languageDetector, searchIndex, crawlerProperties);
    }

    @Override
    protected String docIdFor(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalizeUrl(url).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            // SHA-256 is guaranteed to be available on every JVM; this branch is unreachable.
            return Integer.toHexString(url.hashCode());
        }
    }

    @Override
    protected boolean shouldFollow(String url, IndexingJob job) {
        if (url == null || url.isBlank()) {
            return false;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        if (!isSameHost(url, job.getBaseUrl())) {
            return false;
        }

        String lowerUrl = url.toLowerCase(Locale.ROOT);
        for (String fragment : EXCLUDED_PATH_FRAGMENTS) {
            if (lowerUrl.contains(fragment)) {
                return false;
            }
        }
        for (String extension : EXCLUDED_EXTENSIONS) {
            if (lowerUrl.endsWith(extension)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }
        String withoutFragment = url.contains("#") ? url.substring(0, url.indexOf('#')) : url;
        // Strip tracking / non-content query parameters; 360stepeni.mk pages don't need them.
        String withoutQuery = withoutFragment.contains("?")
            ? withoutFragment.substring(0, withoutFragment.indexOf('?'))
            : withoutFragment;

        // WordPress permalinks on this site are directory-style (trailing slash); normalize
        // to that form so the same page isn't enqueued twice under two different URLs. Leave
        // URLs that point at an actual file (e.g. a sitemap.xml seed) untouched.
        int lastSlash = withoutQuery.lastIndexOf('/');
        String lastSegment = withoutQuery.substring(lastSlash + 1);
        boolean looksLikeFile = lastSegment.contains(".");
        if (looksLikeFile || withoutQuery.endsWith("/")) {
            return withoutQuery;
        }
        return withoutQuery + "/";
    }

    private boolean isSameHost(String url, String baseUrl) {
        try {
            String urlHost = normalizeHost(new URI(url).getHost());
            String baseHost = normalizeHost(new URI(baseUrl).getHost());
            return urlHost != null && urlHost.equals(baseHost);
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    private String normalizeHost(String host) {
        if (host == null) {
            return null;
        }
        String lower = host.toLowerCase(Locale.ROOT);
        return lower.startsWith("www.") ? lower.substring(4) : lower;
    }
}
