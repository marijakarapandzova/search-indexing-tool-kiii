package mk.ukim.finki.searchindexing.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;

/**
 * A page/resource the pipeline fetched from the assigned website, parsed and
 * added to the search index. Once it is part of a {@link DonationBatch} it is
 * on its way to doniraj.vezilka.ai.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "indexed_documents")
public class IndexedDocument extends BaseAuditableEntity {
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private IndexingJob job;

    /**
     * The identifier of this document inside the search index (usually derived
     * from the URL). Used to add/update/delete the document in the index.
     */
    private String docId;

    @Column(nullable = false, length = 2048)
    private String url;

    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private LocalDateTime indexedAt;

    /**
     * Confidence (0.0 - 1.0) that {@code content} is written in Macedonian,
     * produced by the {@code LanguageDetector}.
     */
    private Double macedonianConfidence;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaItem> mediaItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "donation_batch_id")
    private DonationBatch donationBatch;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public IndexedDocument(
        IndexingJob job,
        String docId,
        String url,
        String title,
        String content,
        ResourceType resourceType,
        LocalDateTime indexedAt,
        Double macedonianConfidence
    ) {
        this.job = job;
        this.docId = docId;
        this.url = url;
        this.title = title;
        this.content = content;
        this.resourceType = resourceType;
        this.indexedAt = indexedAt;
        this.macedonianConfidence = macedonianConfidence;
    }
}
