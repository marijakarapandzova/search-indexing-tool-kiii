package mk.ukim.finki.searchindexing.web.handler;

import mk.ukim.finki.searchindexing.model.exception.InvalidJobStateException;
import mk.ukim.finki.searchindexing.model.exception.JobNotFoundException;
import mk.ukim.finki.searchindexing.web.controller.IndexingJobController;
import mk.ukim.finki.searchindexing.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = IndexingJobController.class)
public class IndexingJobControllerExceptionHandler {
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(JobNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiError.of(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(InvalidJobStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(InvalidJobStateException exception) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiError.of(HttpStatus.CONFLICT, exception.getMessage()));
    }
}
