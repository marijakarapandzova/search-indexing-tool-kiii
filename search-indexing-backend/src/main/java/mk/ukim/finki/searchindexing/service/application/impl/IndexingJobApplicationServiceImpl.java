package mk.ukim.finki.searchindexing.service.application.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.events.IndexingJobStartedEvent;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexingJobDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayCrawlActionLogDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayIndexingJobDto;
import mk.ukim.finki.searchindexing.service.application.IndexingJobApplicationService;
import mk.ukim.finki.searchindexing.service.domain.CrawlActionLogService;
import mk.ukim.finki.searchindexing.service.domain.IndexingJobService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class IndexingJobApplicationServiceImpl implements IndexingJobApplicationService {
    private final IndexingJobService indexingJobService;
    private final CrawlActionLogService crawlActionLogService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public IndexingJobApplicationServiceImpl(
        IndexingJobService indexingJobService,
        CrawlActionLogService crawlActionLogService,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.indexingJobService = indexingJobService;
        this.crawlActionLogService = crawlActionLogService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public List<DisplayIndexingJobDto> findAll() {
        return DisplayIndexingJobDto.from(indexingJobService.findAll());
    }

    @Override
    public Optional<DisplayIndexingJobDto> findById(Long id) {
        return indexingJobService.findById(id).map(DisplayIndexingJobDto::from);
    }

    @Override
    public DisplayIndexingJobDto create(CreateIndexingJobDto createIndexingJobDto) {
        IndexingJob job = indexingJobService.create(createIndexingJobDto.toIndexingJob());
        return DisplayIndexingJobDto.from(job);
    }

    @Override
    @Transactional
    public DisplayIndexingJobDto start(Long id) {
        IndexingJob job = indexingJobService.start(id);
        applicationEventPublisher.publishEvent(new IndexingJobStartedEvent(id));
        return DisplayIndexingJobDto.from(job);
    }

    @Override
    public DisplayIndexingJobDto stop(Long id) {
        return DisplayIndexingJobDto.from(indexingJobService.stop(id));
    }

    @Override
    public List<DisplayCrawlActionLogDto> findLogsByJobId(Long id) {
        return DisplayCrawlActionLogDto.from(crawlActionLogService.findByJobId(id));
    }
}
