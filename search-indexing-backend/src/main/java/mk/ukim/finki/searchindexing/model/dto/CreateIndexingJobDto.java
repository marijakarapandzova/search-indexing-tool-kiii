package mk.ukim.finki.searchindexing.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;

public record CreateIndexingJobDto(
    @NotBlank
    String baseUrl,
    String description,
    @NotEmpty
    List<@Valid CreateCrawlSeedDto> seeds
) {
    public IndexingJob toIndexingJob() {
        IndexingJob job = new IndexingJob(baseUrl, description);
        seeds
            .stream()
            .map(seed -> seed.toCrawlSeed(job))
            .forEach(job.getSeeds()::add);
        return job;
    }
}
