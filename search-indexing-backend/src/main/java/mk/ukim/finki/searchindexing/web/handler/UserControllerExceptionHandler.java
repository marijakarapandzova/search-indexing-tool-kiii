package mk.ukim.finki.searchindexing.web.handler;

import mk.ukim.finki.searchindexing.model.exception.IncorrectPasswordException;
import mk.ukim.finki.searchindexing.model.exception.UserNotFoundException;
import mk.ukim.finki.searchindexing.model.exception.UsernameAlreadyExistsException;
import mk.ukim.finki.searchindexing.web.controller.UserController;
import mk.ukim.finki.searchindexing.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserControllerExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameAlreadyExists(UsernameAlreadyExistsException exception) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiError.of(HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class, IncorrectPasswordException.class})
    public ResponseEntity<ApiError> handleInvalidCredentials(RuntimeException exception) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiError.of(HttpStatus.UNAUTHORIZED, exception.getMessage()));
    }
}
