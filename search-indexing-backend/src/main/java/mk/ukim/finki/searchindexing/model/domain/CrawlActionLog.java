package mk.ukim.finki.searchindexing.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;

/**
 * One step of the crawl-index pipeline, persisted so a job's behaviour can be
 * traced and shown live in the frontend.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "crawl_action_logs")
public class CrawlActionLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private IndexingJob job;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CrawlActionType actionType;

    @Column(columnDefinition = "text")
    private String details;

    @Column(nullable = false)
    private Boolean successful;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    public CrawlActionLog(
        IndexingJob job,
        CrawlActionType actionType,
        String details,
        Boolean successful,
        LocalDateTime occurredAt
    ) {
        this.job = job;
        this.actionType = actionType;
        this.details = details;
        this.successful = successful;
        this.occurredAt = occurredAt;
    }
}
