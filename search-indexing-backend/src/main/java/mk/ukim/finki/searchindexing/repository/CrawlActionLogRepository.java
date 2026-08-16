package mk.ukim.finki.searchindexing.repository;

import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlActionLogRepository extends JpaRepository<CrawlActionLog, Long> {
    List<CrawlActionLog> findAllByJobIdOrderByOccurredAtAsc(Long jobId);
}
