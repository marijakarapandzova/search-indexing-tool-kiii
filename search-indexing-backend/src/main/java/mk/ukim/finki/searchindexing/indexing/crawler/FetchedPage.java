package mk.ukim.finki.searchindexing.indexing.crawler;

import java.time.LocalDateTime;

/**
 * The raw result of fetching one URL from the assigned website, handed to the
 * {@code DocumentParser} for parsing.
 *
 * @param url         the URL that was fetched
 * @param statusCode  the HTTP status code (e.g. 200, 404)
 * @param contentType the value of the {@code Content-Type} response header
 * @param html        the raw response body (HTML/XML/text)
 * @param fetchedAt   when the page was fetched
 */
public record FetchedPage(
    String url,
    int statusCode,
    String contentType,
    String html,
    LocalDateTime fetchedAt
) {
    /**
     * @return {@code true} for a 2xx response with a body worth parsing
     */
    public boolean isOk() {
        return statusCode >= 200 && statusCode < 300 && html != null && !html.isBlank();
    }
}
