package com.codementor.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Thin wrapper around the shared AI {@link RestClient} that adds retry with
 * backoff for transient failures. Cloud AI hosts (Render free tier) return
 * gateway 502/503/504 responses while a service is waking up, so a short
 * retry turns a confusing "AI service rejected the request" error into a
 * successful call.
 */
@Component
public class AiClient {

    private static final Logger log = LoggerFactory.getLogger(AiClient.class);

    // A sleeping free-tier Render service can take ~50-60s to wake up, during
    // which its edge returns gateway 502/503/504. The attempts below span
    // roughly a minute so the waking service is given time to come online.
    private static final int MAX_ATTEMPTS = 6;
    private static final long BASE_BACKOFF_MILLIS = 3000L;
    private static final long MAX_BACKOFF_MILLIS = 20000L;

    private final RestClient restClient;

    public AiClient(RestClient aiRestClient) {
        this.restClient = aiRestClient;
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        RestClientResponseException lastStatusError = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return restClient.post()
                        .uri(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(responseType);
            } catch (RestClientResponseException exception) {
                int status = exception.getStatusCode().value();
                if (!isRetryable(status) || attempt == MAX_ATTEMPTS) {
                    throw exception;
                }
                lastStatusError = exception;
                log.warn("AI service returned {} (attempt {}/{}). Retrying...",
                        status, attempt, MAX_ATTEMPTS);
            } catch (ResourceAccessException exception) {
                if (attempt == MAX_ATTEMPTS) {
                    throw exception;
                }
                log.warn("Could not reach the AI service (attempt {}/{}): {}. "
                                + "Retrying...",
                        attempt, MAX_ATTEMPTS, exception.getMessage());
            }

            backoff(attempt);
        }

        throw lastStatusError != null
                ? lastStatusError
                : new IllegalStateException("The AI service is unavailable.");
    }

    private boolean isRetryable(int status) {
        return status == 502 || status == 503 || status == 504;
    }

    private void backoff(int attempt) {
        long delay = Math.min(
                BASE_BACKOFF_MILLIS * (1L << (attempt - 1)),
                MAX_BACKOFF_MILLIS
        );
        try {
            Thread.sleep(delay);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
