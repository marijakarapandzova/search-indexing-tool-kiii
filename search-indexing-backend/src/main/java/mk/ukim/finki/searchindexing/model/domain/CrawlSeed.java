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
import mk.ukim.finki.searchindexing.model.enums.SeedType;

/**
 * A single entry point the crawl-index pipeline starts from,
 * e.g. (URL, "https://mkd.mk/vesti") or (SITEMAP, "https://mkd.mk/sitemap.xml").
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "crawl_seeds")
public class CrawlSeed extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeedType type;

    @Column(nullable = false, length = 2048)
    private String value;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private IndexingJob job;

    public CrawlSeed(SeedType type, String value, IndexingJob job) {
        this.type = type;
        this.value = value;
        this.job = job;
    }
}
