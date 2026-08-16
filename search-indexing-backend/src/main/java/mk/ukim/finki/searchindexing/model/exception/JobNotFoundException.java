package mk.ukim.finki.searchindexing.model.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("An indexing job with id %d does not exist.".formatted(id));
    }
}
