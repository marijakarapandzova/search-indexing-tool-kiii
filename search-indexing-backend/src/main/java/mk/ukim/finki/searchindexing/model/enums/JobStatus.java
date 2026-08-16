package mk.ukim.finki.searchindexing.model.enums;

/**
 * Lifecycle of an {@code IndexingJob}:
 * CREATED -> RUNNING -> COMPLETED | FAILED | STOPPED
 */
public enum JobStatus {
    CREATED,
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED
}
