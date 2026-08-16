package mk.ukim.finki.searchindexing.model.dto;

import java.util.List;
import mk.ukim.finki.searchindexing.indexing.search.SearchHit;

/**
 * A single search result: enough to render a hit and link back to the
 * indexed document it came from.
 */
public record SearchHitDto(
    String docId,
    String url,
    String title,
    String snippet,
    double score,
    Double macedonianConfidence
) {
    public static SearchHitDto from(SearchHit hit) {
        return new SearchHitDto(
            hit.docId(),
            hit.url(),
            hit.title(),
            hit.snippet(),
            hit.score(),
            hit.macedonianConfidence()
        );
    }

    public static List<SearchHitDto> from(List<SearchHit> hits) {
        return hits
            .stream()
            .map(SearchHitDto::from)
            .toList();
    }
}
