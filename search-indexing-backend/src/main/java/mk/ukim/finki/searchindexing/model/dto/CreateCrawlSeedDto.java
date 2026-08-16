package mk.ukim.finki.searchindexing.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mk.ukim.finki.searchindexing.model.domain.CrawlSeed;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.SeedType;

public record CreateCrawlSeedDto(
    @NotNull
    SeedType type,
    @NotBlank
    String value
) {
    public CrawlSeed toCrawlSeed(IndexingJob job) {
        return new CrawlSeed(type, value, job);
    }
}
