package mk.ukim.finki.searchindexing.repository;

import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexingJobRepository extends JpaRepository<IndexingJob, Long> {
    // TODO(student): Add the derived or custom queries your services need
    //  (e.g. find jobs by status, by base URL, ...).
}
