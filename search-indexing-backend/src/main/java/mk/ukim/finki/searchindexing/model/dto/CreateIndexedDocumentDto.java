package mk.ukim.finki.searchindexing.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;

/**
 * A document as produced by the {@code DocumentParser}, before it is
 * persisted and attached to a job.
 */
public record CreateIndexedDocumentDto(
    String docId,
    String url,
    String title,
    String content,
    ResourceType resourceType,
    LocalDateTime indexedAt,
    Double macedonianConfidence,
    List<CreateMediaItemDto> mediaItems
) {
    public CreateIndexedDocumentDto withMacedonianConfidence(Double confidence) {
        return new CreateIndexedDocumentDto(
            docId,
            url,
            title,
            content,
            resourceType,
            indexedAt,
            confidence,
            mediaItems
        );
    }

    public IndexedDocument toIndexedDocument(IndexingJob job) {
        IndexedDocument document = new IndexedDocument(
            job,
            docId,
            url,
            title,
            content,
            resourceType,
            indexedAt,
            macedonianConfidence
        );
        if (mediaItems != null) {
            mediaItems
                .stream()
                .map(mediaItem -> mediaItem.toMediaItem(document))
                .forEach(document.getMediaItems()::add);
        }
        return document;
    }
}
