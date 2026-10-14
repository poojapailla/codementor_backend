package com.codementor.backend.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Single shared {@link RestClient} for all calls to the Python AI service.
 * Centralising it keeps timeouts consistent and lets connection pooling and
 * retry logic live in one place.
 */
@Configuration
public class AiClientConfig {

    @Bean
    public RestClient aiRestClient(
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        // Render free-tier services cold-start in roughly 30-60s. A generous
        // connect timeout lets the request wait for the first byte instead of
        // failing fast; the retry layer also re-tries platform 502/503/504s.
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));

        // Local/free-tier inference is slow. Keep this above the AI service's
        // own request timeout so its clean 504 reaches us rather than a
        // dropped socket.
        requestFactory.setReadTimeout(Duration.ofMinutes(10));

        return RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
