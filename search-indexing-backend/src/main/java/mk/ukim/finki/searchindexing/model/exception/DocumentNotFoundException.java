package mk.ukim.finki.searchindexing.model.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(Long id) {
        super("An indexed document with id %d does not exist.".formatted(id));
    }
}
