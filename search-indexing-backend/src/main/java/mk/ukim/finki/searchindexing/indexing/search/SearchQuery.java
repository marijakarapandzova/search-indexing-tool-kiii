package mk.ukim.finki.searchindexing.indexing.search;

/**
 * A full-text query against the search index.
 *
 * @param query                   the raw query string (Macedonian text)
 * @param page                    zero-based page index
 * @param size                    number of hits per page
 * @param minMacedonianConfidence optional lower bound on a document's language
 *                                confidence; {@code null} means "do not filter"
 */
public record SearchQuery(
    String query,
    int page,
    int size,
    Double minMacedonianConfidence
) {
}
