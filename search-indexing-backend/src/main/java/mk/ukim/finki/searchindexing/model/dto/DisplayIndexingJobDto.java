package mk.ukim.finki.searchindexing.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.JobStatus;

public record DisplayIndexingJobDto(
    Long id,
    String baseUrl,
    JobStatus status,
    String description,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    List<DisplayCrawlSeedDto> seeds
) {
    public static DisplayIndexingJobDto from(IndexingJob job) {
        return new DisplayIndexingJobDto(
            job.getId(),
            job.getBaseUrl(),
            job.getStatus(),
            job.getDescription(),
            job.getStartedAt(),
            job.getFinishedAt(),
            DisplayCrawlSeedDto.from(job.getSeeds())
        );
    }

    public static List<DisplayIndexingJobDto> from(List<IndexingJob> jobs) {
        return jobs
            .stream()
            .map(DisplayIndexingJobDto::from)
            .toList();
    }
}
