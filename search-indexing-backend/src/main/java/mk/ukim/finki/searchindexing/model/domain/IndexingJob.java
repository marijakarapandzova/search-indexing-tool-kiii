package mk.ukim.finki.searchindexing.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.searchindexing.model.enums.JobStatus;

/**
 * One crawl-and-index run over the assigned website: which site it works
 * against ({@code baseUrl}), where it should start (the {@link CrawlSeed}s)
 * and how far it has come.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "indexing_jobs")
public class IndexingJob extends BaseAuditableEntity {
    /**
     * The base URL of the website this job crawls, e.g. {@code https://mkd.mk}.
     */
    @Column(nullable = false, length = 2048)
    private String baseUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String description;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrawlSeed> seeds = new ArrayList<>();

    public IndexingJob(String baseUrl, String description) {
        this.baseUrl = baseUrl;
        this.description = description;
        this.status = JobStatus.CREATED;
    }
}
