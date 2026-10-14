package com.codementor.backend.service;

import com.codementor.backend.dto.CodeReviewResponseDTO;
import com.codementor.backend.model.CodeSubmission;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    // Mirrors the AI service's CodeReviewRequest.code max_length so oversized
    // input is rejected here with a clear message instead of surfacing as an
    // opaque upstream validation error.
    public static final int MAX_CODE_LENGTH = 20000;

    private final AiClient aiClient;

    public AIService(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    public CodeReviewResponseDTO reviewCode(
            CodeSubmission submission
    ) {
        String code = submission.getCode();

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide code to review."
            );
        }

        if (code.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Your code is " + code.length() + " characters. "
                            + "Please submit " + MAX_CODE_LENGTH
                            + " characters or fewer at a time."
            );
        }

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("code", code);
        requestBody.put("language", submission.getLanguage());
        requestBody.put("problem_description",
                submission.getProblemDescription());

        CodeReviewResponseDTO response = aiClient.post(
                "/review",
                requestBody,
                CodeReviewResponseDTO.class
        );

        if (response == null) {
            throw new IllegalStateException(
                    "The AI service returned an empty review. Please try again."
            );
        }

        return response;
    }
}
