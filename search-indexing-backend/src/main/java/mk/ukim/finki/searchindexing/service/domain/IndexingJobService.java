package mk.ukim.finki.searchindexing.service.domain;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;

/**
 * Domain service for indexing jobs. Works with entities only —
 * DTO mapping happens one layer above, in the application service.
 */
public interface IndexingJobService {
    List<IndexingJob> findAll();

    Optional<IndexingJob> findById(Long id);

    IndexingJob create(IndexingJob job);

    /**
     * Transitions a CREATED or STOPPED job to RUNNING and stamps
     * {@code startedAt}. Throws {@code JobNotFoundException} or
     * {@code InvalidJobStateException} accordingly.
     */
    IndexingJob start(Long id);

    /**
     * Transitions a RUNNING job to STOPPED.
     */
    IndexingJob stop(Long id);

    /**
     * Transitions a RUNNING job to COMPLETED and stamps {@code finishedAt}.
     */
    IndexingJob complete(Long id);

    /**
     * Transitions a job to FAILED and stamps {@code finishedAt}.
     */
    IndexingJob fail(Long id);
}
