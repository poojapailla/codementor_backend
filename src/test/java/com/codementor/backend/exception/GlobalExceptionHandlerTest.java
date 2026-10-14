package com.codementor.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsInvalidCredentialsToUnauthorized() {
        ResponseEntity<Map<String, String>> response =
                handler.handleRuntimeException(
                        new RuntimeException("Invalid email or password"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password",
                response.getBody().get("message"));
    }

    @Test
    void mapsDuplicateEmailToConflict() {
        ResponseEntity<Map<String, String>> response =
                handler.handleRuntimeException(
                        new RuntimeException("Email already registered"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void mapsUnknownUsersAndSubmissionsToNotFound() {
        assertEquals(HttpStatus.NOT_FOUND,
                handler.handleRuntimeException(
                        new RuntimeException("User not found"))
                        .getStatusCode());

        assertEquals(HttpStatus.NOT_FOUND,
                handler.handleRuntimeException(
                        new RuntimeException("Code submission not found"))
                        .getStatusCode());
    }

    @Test
    void mapsValidationErrorsToBadRequest() {
        ResponseEntity<Map<String, String>> response =
                handler.handleIllegalArgument(
                        new IllegalArgumentException("Please provide code."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please provide code.",
                response.getBody().get("message"));
    }

    @Test
    void hidesInternalDetailsForUnexpectedErrors() {
        ResponseEntity<Map<String, String>> response =
                handler.handleRuntimeException(
                        new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertEquals("An unexpected server error occurred",
                response.getBody().get("message"));
    }

    @Test
    void forwardsFastApiValidationDetailArray() {
        String body = "{\"detail\":[{\"type\":\"string_too_long\","
                + "\"loc\":[\"body\",\"code\"],"
                + "\"msg\":\"String should have at most 20000 characters\","
                + "\"input\":\"aaa\"}]}";

        RestClientResponseException exception = new RestClientResponseException(
                "422 Unprocessable Entity",
                422,
                "Unprocessable Entity",
                new HttpHeaders(),
                body.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        ResponseEntity<Map<String, String>> response =
                handler.handleRestClientError(exception);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("String should have at most 20000 characters",
                response.getBody().get("message"));
    }

    @Test
    void forwardsPlainTextDetailFromStringBody() {
        String body = "{\"detail\":\"Code and language are required.\"}";

        RestClientResponseException exception = new RestClientResponseException(
                "422 Unprocessable Entity",
                422,
                "Unprocessable Entity",
                new HttpHeaders(),
                body.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        ResponseEntity<Map<String, String>> response =
                handler.handleRestClientError(exception);

        assertEquals("Code and language are required.",
                response.getBody().get("message"));
    }
}
