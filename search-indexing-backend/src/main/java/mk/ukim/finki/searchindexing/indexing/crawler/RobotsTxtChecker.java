package mk.ukim.finki.searchindexing.indexing.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.config.CrawlerProperties;
import org.springframework.stereotype.Component;

/**
 * A small, best-effort {@code robots.txt} checker: fetches and caches
 * {@code /robots.txt} per host and answers whether a given path is allowed
 * for the configured user agent (falling back to the {@code *} group).
 *
 * <p>This only understands the common {@code User-agent}/{@code Disallow}/
 * {@code Allow} directives, which is enough to be a polite crawler without
 * pulling in a full robots-exclusion library.</p>
 */
@Component
@Slf4j
public class RobotsTxtChecker {
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    private final Map<String, List<String>> disallowRulesByHost = new ConcurrentHashMap<>();
    private final CrawlerProperties crawlerProperties;

    public RobotsTxtChecker(CrawlerProperties crawlerProperties) {
        this.crawlerProperties = crawlerProperties;
    }

    public boolean isAllowed(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getScheme() + "://" + uri.getAuthority();
            List<String> disallowed = disallowRulesByHost.computeIfAbsent(host, this::fetchDisallowRules);
            String path = uri.getRawPath() == null ? "/" : uri.getRawPath();
            for (String rule : disallowed) {
                if (!rule.isEmpty() && path.startsWith(rule)) {
                    return false;
                }
            }
            return true;
        } catch (URISyntaxException exception) {
            return true;
        }
    }

    private List<String> fetchDisallowRules(String host) {
        List<String> rules = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + "/robots.txt"))
                .header("User-Agent", crawlerProperties.userAgent() == null
                    ? "EMC-SearchIndexingBot/1.0" : crawlerProperties.userAgent())
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                parseRobotsTxt(response.body(), rules);
            }
        } catch (Exception exception) {
            log.debug("Could not fetch robots.txt for {}: {}", host, exception.getMessage());
        }
        return rules;
    }

    private void parseRobotsTxt(String body, List<String> rules) {
        boolean inRelevantGroup = false;
        boolean sawSpecificAgent = false;
        for (String rawLine : body.split("\n")) {
            String line = rawLine.split("#", 2)[0].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            String directive = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            if (directive.equals("user-agent")) {
                boolean isWildcard = value.equals("*");
                inRelevantGroup = isWildcard && !sawSpecificAgent;
            } else if (directive.equals("disallow") && inRelevantGroup && !value.isEmpty()) {
                rules.add(value);
            }
        }
    }
}
