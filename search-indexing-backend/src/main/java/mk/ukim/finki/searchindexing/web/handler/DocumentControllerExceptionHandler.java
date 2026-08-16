package mk.ukim.finki.searchindexing.web.handler;

import mk.ukim.finki.searchindexing.model.exception.DocumentNotFoundException;
import mk.ukim.finki.searchindexing.web.controller.DocumentController;
import mk.ukim.finki.searchindexing.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DocumentController.class)
public class DocumentControllerExceptionHandler {
    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(DocumentNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiError.of(HttpStatus.NOT_FOUND, exception.getMessage()));
    }
}
