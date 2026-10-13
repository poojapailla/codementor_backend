package com.codementor.backend.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception) {

        log.warn("Bad request: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleRestClientError(
            RestClientResponseException exception) {

        int upstreamStatus = exception.getStatusCode().value();
        String upstreamDetail =
                extractDetail(exception.getResponseBodyAsString());

        HttpStatus status = switch (upstreamStatus) {
            case 400, 401, 403, 404, 409, 422, 502, 503, 504 ->
                    HttpStatus.valueOf(upstreamStatus);
            default -> HttpStatus.BAD_GATEWAY;
        };

        String message = upstreamStatus >= 500 && upstreamStatus != 502
                && upstreamStatus != 503 && upstreamStatus != 504
                ? "The AI service returned an unexpected response. Please try again."
                : (upstreamDetail != null
                        ? upstreamDetail
                        : "The AI service rejected the request.");

        log.warn("AI service call failed with upstream status {}: {}",
                upstreamStatus, message);

        return ResponseEntity.status(status)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception) {

        String message = exception.getMessage();

        if ("Invalid email or password".equals(message)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        if ("Email already registered".equals(message)
                || "This code submission has already been reviewed".equals(message)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", message));
        }

        if ("User not found".equals(message)
                || "Code submission not found".equals(message)
                || "Code review not found".equals(message)
                || "Review not found for this submission".equals(message)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", message));
        }

        log.error("Unhandled server error", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected server error occurred"));
    }

    private String extractDetail(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern
                        .compile("\\\"(detail|message)\\\"\\s*:\\s*\\\"((?:[^\\\"\\\\]|\\\\.)*)\\\"")
                        .matcher(responseBody);

        if (matcher.find()) {
            return matcher.group(2)
                    .replace("\\\"", "\"")
                    .replace("\\n", " ");
        }

        return null;
    }
}
