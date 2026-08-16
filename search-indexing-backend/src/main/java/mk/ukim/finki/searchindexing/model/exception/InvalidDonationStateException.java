package mk.ukim.finki.searchindexing.model.exception;

import mk.ukim.finki.searchindexing.model.enums.DonationStatus;

public class InvalidDonationStateException extends RuntimeException {
    public InvalidDonationStateException(Long id, DonationStatus status) {
        super("The donation batch with id %d cannot perform this operation in status %s.".formatted(id, status));
    }
}
