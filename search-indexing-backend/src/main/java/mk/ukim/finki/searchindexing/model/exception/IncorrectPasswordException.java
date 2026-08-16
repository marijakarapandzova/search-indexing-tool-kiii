package mk.ukim.finki.searchindexing.model.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("The password is incorrect.");
    }
}
