package mk.ukim.finki.searchindexing.web.handler;

import mk.ukim.finki.searchindexing.model.exception.DocumentNotFoundException;
import mk.ukim.finki.searchindexing.model.exception.DonationBatchNotFoundException;
import mk.ukim.finki.searchindexing.model.exception.InvalidDonationStateException;
import mk.ukim.finki.searchindexing.model.exception.VezilkaIntegrationException;
import mk.ukim.finki.searchindexing.web.controller.DonationController;
import mk.ukim.finki.searchindexing.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DonationController.class)
public class DonationControllerExceptionHandler {
    @ExceptionHandler({DonationBatchNotFoundException.class, DocumentNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiError.of(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(InvalidDonationStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(InvalidDonationStateException exception) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiError.of(HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler(VezilkaIntegrationException.class)
    public ResponseEntity<ApiError> handleVezilkaFailure(VezilkaIntegrationException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ApiError.of(HttpStatus.BAD_GATEWAY, exception.getMessage()));
    }
}
