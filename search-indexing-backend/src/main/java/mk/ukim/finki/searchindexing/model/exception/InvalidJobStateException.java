package mk.ukim.finki.searchindexing.model.exception;

import mk.ukim.finki.searchindexing.model.enums.JobStatus;

public class InvalidJobStateException extends RuntimeException {
    public InvalidJobStateException(Long id, JobStatus status) {
        super("The indexing job with id %d cannot perform this operation in status %s.".formatted(id, status));
    }
}
