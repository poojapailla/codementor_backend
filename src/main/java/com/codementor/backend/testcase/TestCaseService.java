package com.codementor.backend.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.codementor.backend.service.AiClient;

@Service
public class TestCaseService {

    private static final int MAX_CODE_LENGTH = 20000;

    private final AiClient aiClient;

    public TestCaseService(AiClient aiClient) {
        this.aiClient = aiClient;
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

        Map<?, ?> response = aiClient.post(
                "/generate-tests",
                aiRequest,
                Map.class
        );

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
