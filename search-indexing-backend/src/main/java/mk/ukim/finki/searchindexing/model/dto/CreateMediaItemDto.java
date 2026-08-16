package mk.ukim.finki.searchindexing.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.domain.MediaItem;
import mk.ukim.finki.searchindexing.model.enums.MediaType;

public record CreateMediaItemDto(
    @NotNull
    MediaType type,
    @NotBlank
    String sourceUrl,
    String storagePath
) {
    public MediaItem toMediaItem(IndexedDocument document) {
        return new MediaItem(document, type, sourceUrl, storagePath);
    }
}
