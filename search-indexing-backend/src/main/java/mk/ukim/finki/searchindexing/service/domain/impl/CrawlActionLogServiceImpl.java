package mk.ukim.finki.searchindexing.service.domain.impl;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlActionLog;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;
import mk.ukim.finki.searchindexing.repository.CrawlActionLogRepository;
import mk.ukim.finki.searchindexing.service.domain.CrawlActionLogService;
import org.springframework.stereotype.Service;

@Service
public class CrawlActionLogServiceImpl implements CrawlActionLogService {
    private final CrawlActionLogRepository crawlActionLogRepository;

    public CrawlActionLogServiceImpl(CrawlActionLogRepository crawlActionLogRepository) {
        this.crawlActionLogRepository = crawlActionLogRepository;
    }

    @Override
    public CrawlActionLog log(IndexingJob job, CrawlActionType actionType, String details, boolean successful) {
        return crawlActionLogRepository.save(new CrawlActionLog(
            job,
            actionType,
            details,
            successful,
            LocalDateTime.now()
        ));
    }

    @Override
    public List<CrawlActionLog> findByJobId(Long jobId) {
        return crawlActionLogRepository.findAllByJobIdOrderByOccurredAtAsc(jobId);
    }
}
