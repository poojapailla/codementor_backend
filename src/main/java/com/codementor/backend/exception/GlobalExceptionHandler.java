package com.codementor.backend.exception;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Pattern STRING_DETAIL = Pattern.compile(
            "\"(detail|message)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    // FastAPI validation errors look like:
    // {"detail":[{"type":"string_too_long","loc":["body","code"],
    //             "msg":"String should have at most 20000 characters",...}]}
    private static final Pattern VALIDATION_MESSAGE = Pattern.compile(
            "\"msg\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception) {

        log.warn("Bad request: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", safeMessage(
                        exception.getMessage(),
                        "The request was invalid.")));
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

        String message;

        if (upstreamDetail != null) {
            message = upstreamDetail;
        } else if (upstreamStatus >= 500) {
            message = "The AI service is temporarily unavailable. "
                    + "Please try again in a moment.";
        } else {
            message = "The AI service could not process this request "
                    + "(status " + upstreamStatus + ").";
        }

        log.warn("AI service call failed with upstream status {}: {}",
                upstreamStatus, message);

        return ResponseEntity.status(status)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleAiServiceUnreachable(
            ResourceAccessException exception) {

        log.warn("AI service unreachable: {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message",
                        "The AI service is starting up or temporarily "
                        + "unavailable. Please try again in a few seconds."));
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

    private String safeMessage(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }

    private String extractDetail(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        String body = responseBody.trim();

        String structured = firstGroup(STRING_DETAIL, body, 2);
        if (structured != null) {
            return unescape(structured);
        }

        String validation = firstGroup(VALIDATION_MESSAGE, body, 1);
        if (validation != null) {
            return unescape(validation);
        }

        // Plain-text (non-HTML) error bodies from a proxy, e.g. "Bad Gateway".
        if (!body.startsWith("<")) {
            String cleaned = body.replaceAll("\\s+", " ").trim();
            if (!cleaned.isEmpty()) {
                return cleaned.length() > 300
                        ? cleaned.substring(0, 300) + "..."
                        : cleaned;
            }
        }

        return null;
    }

    private String firstGroup(Pattern pattern, String input, int group) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(group) : null;
    }

    private String unescape(String value) {
        return value.replace("\\\"", "\"")
                .replace("\\n", " ")
                .replace("\\t", " ");
    }
}
