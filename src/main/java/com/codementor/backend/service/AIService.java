package com.codementor.backend.service;

import com.codementor.backend.dto.CodeReviewResponseDTO;
import com.codementor.backend.model.CodeSubmission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    private final RestClient restClient;

    public AIService(
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        // Local CPU inference is slow. Keep this longer than the AI service's own
        // request timeout so its 504 error is received instead of a dropped socket.
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofMinutes(10));

        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public CodeReviewResponseDTO reviewCode(
            CodeSubmission submission
    ) {

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put(
                "code",
                submission.getCode()
        );

        requestBody.put(
                "language",
                submission.getLanguage()
        );

        requestBody.put(
                "problem_description",
                submission.getProblemDescription()
        );

        return restClient
                .post()
                .uri("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(CodeReviewResponseDTO.class);
    }
}