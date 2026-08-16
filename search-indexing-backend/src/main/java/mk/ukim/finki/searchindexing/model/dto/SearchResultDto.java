package mk.ukim.finki.searchindexing.model.dto;

import java.util.List;
import mk.ukim.finki.searchindexing.indexing.search.SearchResult;

/**
 * The response for a full-text search: the total number of matches and the
 * hits for the requested page.
 */
public record SearchResultDto(
    String query,
    long totalHits,
    List<SearchHitDto> hits
) {
    public static SearchResultDto from(SearchResult result) {
        return new SearchResultDto(
            result.query(),
            result.totalHits(),
            SearchHitDto.from(result.hits())
        );
    }
}
