package mk.ukim.finki.searchindexing.indexing.search;

import java.util.List;

/**
 * The outcome of one search: the total number of matching documents and the
 * hits for the requested page.
 *
 * @param query     the query that produced this result (echoed back)
 * @param totalHits the total number of matching documents in the index
 * @param hits      the hits for the requested page, ordered by relevance
 */
public record SearchResult(
    String query,
    long totalHits,
    List<SearchHit> hits
) {
}
