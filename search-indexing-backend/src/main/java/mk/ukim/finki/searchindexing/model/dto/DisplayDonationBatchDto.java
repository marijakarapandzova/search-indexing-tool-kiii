package mk.ukim.finki.searchindexing.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.DonationBatch;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.enums.DonationStatus;

public record DisplayDonationBatchDto(
    Long id,
    DonationStatus status,
    String vezilkaReference,
    LocalDateTime submittedAt,
    LocalDateTime createdAt,
    List<Long> documentIds
) {
    public static DisplayDonationBatchDto from(DonationBatch batch) {
        return new DisplayDonationBatchDto(
            batch.getId(),
            batch.getStatus(),
            batch.getVezilkaReference(),
            batch.getSubmittedAt(),
            batch.getCreatedAt(),
            batch.getDocuments()
                .stream()
                .map(IndexedDocument::getId)
                .toList()
        );
    }

    public static List<DisplayDonationBatchDto> from(List<DonationBatch> batches) {
        return batches
            .stream()
            .map(DisplayDonationBatchDto::from)
            .toList();
    }
}
