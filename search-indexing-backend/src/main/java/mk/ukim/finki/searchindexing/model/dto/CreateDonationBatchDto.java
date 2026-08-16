package mk.ukim.finki.searchindexing.model.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Request for grouping already-indexed documents into a donation batch.
 */
public record CreateDonationBatchDto(
    @NotEmpty
    List<Long> documentIds
) {
}
