package mk.ukim.finki.searchindexing.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.searchindexing.model.enums.MediaType;

/**
 * An image or video found on an {@link IndexedDocument}.
 * {@code storagePath} points to a locally downloaded copy, when the
 * implementation chooses to download media.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media_items")
public class MediaItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private IndexedDocument document;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType type;

    @Column(nullable = false, length = 2048)
    private String sourceUrl;

    @Column(length = 2048)
    private String storagePath;

    public MediaItem(IndexedDocument document, MediaType type, String sourceUrl, String storagePath) {
        this.document = document;
        this.type = type;
        this.sourceUrl = sourceUrl;
        this.storagePath = storagePath;
    }
}
