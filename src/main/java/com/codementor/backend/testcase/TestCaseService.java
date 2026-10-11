package com.codementor.backend.testcase;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TestCaseService {

    private static final int MAX_CODE_LENGTH = 20000;

    private final RestClient restClient;

    public TestCaseService(
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofMinutes(10));

        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public TestCaseResponseDTO generateTestCases(
            TestCaseRequestDTO request
    ) {

        if (request == null
                || request.getCode() == null
                || request.getCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide code for test case generation."
            );
        }

        if (request.getCode().length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Please submit less than " + MAX_CODE_LENGTH
                            + " characters of code at once."
            );
        }

        Map<String, Object> aiRequest = new HashMap<>();

        aiRequest.put("code", request.getCode());
        aiRequest.put("language",
                request.getLanguage() == null || request.getLanguage().isBlank()
                        ? "text"
                        : request.getLanguage());
        aiRequest.put("problem_description", request.getProblemDescription());

        Map<?, ?> response = restClient.post()
                .uri("/generate-tests")
                .contentType(MediaType.APPLICATION_JSON)
                .body(aiRequest)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "The AI service returned an empty response."
            );
        }

        Object rawCases = response.get("test_cases");

        if (!(rawCases instanceof List<?> caseList) || caseList.isEmpty()) {
            throw new IllegalStateException(
                    "The AI service did not return any test cases. Please try again."
            );
        }

        List<TestCaseResponseDTO.TestCase> testCases = caseList.stream()
                .filter(item -> item instanceof Map<?, ?>)
                .map(item -> toTestCase((Map<?, ?>) item))
                .toList();

        if (testCases.isEmpty()) {
            throw new IllegalStateException(
                    "The AI service returned malformed test cases. Please try again."
            );
        }

        Object notes = response.get("notes");
        String noteText = notes == null || notes.toString().isBlank()
                ? "Expected outputs are inferred from reading the code and "
                        + "have not been executed."
                : notes.toString().trim();

        return new TestCaseResponseDTO(testCases, noteText);
    }

    private TestCaseResponseDTO.TestCase toTestCase(Map<?, ?> item) {
        return new TestCaseResponseDTO.TestCase(
                value(item, "name"),
                value(item, "category"),
                value(item, "input"),
                value(item, "expected_output"),
                value(item, "explanation")
        );
    }

    private String value(Map<?, ?> item, String key) {
        Object value = item.get(key);
        return value == null || value.toString().isBlank()
                ? "Not provided"
                : value.toString().trim();
    }
}
