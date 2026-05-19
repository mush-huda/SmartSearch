package com.huda.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ApiKeyMissingException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> handleMissingApiKey(Exception ex) {
        log.warn("API key is missing: {}", ex.getMessage());
        var response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(BAD_REQUEST.value())
                .error("Bad Request")
                .messages(singletonList(ex.getMessage()))
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        var response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .messages(singletonList(ex.getMessage()))
                .build();
        return ResponseEntity.status(TOO_MANY_REQUESTS).body(response);
    }
}
