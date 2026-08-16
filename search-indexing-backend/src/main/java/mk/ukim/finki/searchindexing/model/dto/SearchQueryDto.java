package mk.ukim.finki.searchindexing.model.dto;

import jakarta.validation.constraints.NotBlank;
import mk.ukim.finki.searchindexing.indexing.search.SearchQuery;

/**
 * A full-text search request coming from the frontend.
 *
 * @param query                   the query string (Macedonian text)
 * @param page                    zero-based page index
 * @param size                    page size
 * @param minMacedonianConfidence optional lower bound on language confidence
 */
public record SearchQueryDto(
    @NotBlank
    String query,
    Integer page,
    Integer size,
    Double minMacedonianConfidence
) {
    public SearchQuery toSearchQuery() {
        return new SearchQuery(
            query,
            page == null ? 0 : page,
            size == null ? 10 : size,
            minMacedonianConfidence
        );
    }
}
