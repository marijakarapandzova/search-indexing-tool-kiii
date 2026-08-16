package mk.ukim.finki.searchindexing.repository;

import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.DonationBatch;
import mk.ukim.finki.searchindexing.model.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationBatchRepository extends JpaRepository<DonationBatch, Long> {
    List<DonationBatch> findAllByStatus(DonationStatus status);
}
