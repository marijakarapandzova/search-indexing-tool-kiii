package mk.ukim.finki.searchindexing.model.dto;

import mk.ukim.finki.searchindexing.model.enums.ResourceType;

/**
 * Optional filters for browsing indexed documents.
 * Any field may be {@code null}, meaning "do not filter by this".
 *
 * @param jobId                   only documents indexed by this job
 * @param resourceType            only documents of this resource type
 * @param minMacedonianConfidence only documents at or above this language confidence
 * @param donated                 {@code true} = only documents already in a donation batch,
 *                                {@code false} = only documents not yet donated
 * @param search                  free-text search over the document title/content
 */
public record DocumentFilterDto(
    Long jobId,
    ResourceType resourceType,
    Double minMacedonianConfidence,
    Boolean donated,
    String search
) {
}
