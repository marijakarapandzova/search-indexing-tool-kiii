package mk.ukim.finki.searchindexing.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;

public record DisplayIndexedDocumentDto(
    Long id,
    Long jobId,
    String docId,
    String url,
    String title,
    String content,
    ResourceType resourceType,
    LocalDateTime indexedAt,
    Double macedonianConfidence,
    List<DisplayMediaItemDto> mediaItems,
    Long donationBatchId
) {
    public static DisplayIndexedDocumentDto from(IndexedDocument document) {
        return new DisplayIndexedDocumentDto(
            document.getId(),
            document.getJob().getId(),
            document.getDocId(),
            document.getUrl(),
            document.getTitle(),
            document.getContent(),
            document.getResourceType(),
            document.getIndexedAt(),
            document.getMacedonianConfidence(),
            DisplayMediaItemDto.from(document.getMediaItems()),
            document.getDonationBatch() == null ? null : document.getDonationBatch().getId()
        );
    }

    public static List<DisplayIndexedDocumentDto> from(List<IndexedDocument> documents) {
        return documents
            .stream()
            .map(DisplayIndexedDocumentDto::from)
            .toList();
    }
}
