package mk.ukim.finki.searchindexing.service.application;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexingJobDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayCrawlActionLogDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayIndexingJobDto;

/**
 * Application service for indexing jobs: orchestrates the domain services and
 * maps between entities and DTOs.
 */
public interface IndexingJobApplicationService {
    List<DisplayIndexingJobDto> findAll();

    Optional<DisplayIndexingJobDto> findById(Long id);

    DisplayIndexingJobDto create(CreateIndexingJobDto createIndexingJobDto);

    /**
     * Starts the job and publishes an {@code IndexingJobStartedEvent} so the
     * crawl-index run happens asynchronously, outside of the web request.
     */
    DisplayIndexingJobDto start(Long id);

    DisplayIndexingJobDto stop(Long id);

    List<DisplayCrawlActionLogDto> findLogsByJobId(Long id);
}
