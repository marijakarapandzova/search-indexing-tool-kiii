package mk.ukim.finki.searchindexing.service.domain.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.enums.JobStatus;
import mk.ukim.finki.searchindexing.model.exception.InvalidJobStateException;
import mk.ukim.finki.searchindexing.model.exception.JobNotFoundException;
import mk.ukim.finki.searchindexing.repository.IndexingJobRepository;
import mk.ukim.finki.searchindexing.service.domain.IndexingJobService;
import org.springframework.stereotype.Service;

@Service
public class IndexingJobServiceImpl implements IndexingJobService {
    private final IndexingJobRepository indexingJobRepository;

    public IndexingJobServiceImpl(IndexingJobRepository indexingJobRepository) {
        this.indexingJobRepository = indexingJobRepository;
    }

    @Override
    public List<IndexingJob> findAll() {
        return indexingJobRepository.findAll();
    }

    @Override
    public Optional<IndexingJob> findById(Long id) {
        return indexingJobRepository.findById(id);
    }

    @Override
    public IndexingJob create(IndexingJob job) {
        return indexingJobRepository.save(job);
    }

    @Override
    public IndexingJob start(Long id) {
        IndexingJob job = findOrThrow(id);
        if (job.getStatus() != JobStatus.CREATED && job.getStatus() != JobStatus.STOPPED) {
            throw new InvalidJobStateException(id, job.getStatus());
        }
        job.setStatus(JobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        job.setFinishedAt(null);
        return indexingJobRepository.save(job);
    }

    @Override
    public IndexingJob stop(Long id) {
        IndexingJob job = findOrThrow(id);
        if (job.getStatus() != JobStatus.RUNNING) {
            throw new InvalidJobStateException(id, job.getStatus());
        }
        job.setStatus(JobStatus.STOPPED);
        job.setFinishedAt(LocalDateTime.now());
        return indexingJobRepository.save(job);
    }

    @Override
    public IndexingJob complete(Long id) {
        IndexingJob job = findOrThrow(id);
        job.setStatus(JobStatus.COMPLETED);
        job.setFinishedAt(LocalDateTime.now());
        return indexingJobRepository.save(job);
    }

    @Override
    public IndexingJob fail(Long id) {
        IndexingJob job = findOrThrow(id);
        job.setStatus(JobStatus.FAILED);
        job.setFinishedAt(LocalDateTime.now());
        return indexingJobRepository.save(job);
    }

    private IndexingJob findOrThrow(Long id) {
        return indexingJobRepository.findById(id).orElseThrow(() -> new JobNotFoundException(id));
    }
}
