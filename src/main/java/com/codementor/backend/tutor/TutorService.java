package com.codementor.backend.tutor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class TutorService {

    private final RestClient restClient;

    public TutorService(
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

    public TutorResponseDTO teach(TutorRequestDTO request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide code for the AI Tutor to explain."
            );
        }

        if (request.getCode().length() > 15000) {
            throw new IllegalArgumentException(
                    "Please submit less than 15000 characters of code at once. "
                    + "Split the program into a smaller section."
            );
        }

        String language = request.getLanguage() == null
                ? "Unknown" : request.getLanguage();
        String level = request.getSkillLevel() == null
                ? "Beginner" : request.getSkillLevel();
        String question = request.getQuestion() == null
                ? "Explain this code step by step."
                : request.getQuestion();

        String prompt = """
                Act as a friendly programming tutor for a %s student.
                Programming language: %s
                Student question: %s

                Explain this code in simple language:
                %s

                Give a clear explanation, a numbered step-by-step guide,
                a complete corrected code example, and one practice tip.
                Do not claim you executed the code.
                """.formatted(level, language, question, request.getCode());

        Map<String, Object> aiRequest = Map.of(
                "code", prompt,
                "language", "text",
                "problem_description",
                "Teach the student clearly. Return concise, useful sections."
        );

        Map<?, ?> response = restClient.post()
                .uri("/review")
                .body(aiRequest)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("The AI service returned an empty response.");
        }

        return new TutorResponseDTO(
                getValue(response, "explanation"),
                getValue(response, "bug_analysis"),
                getValue(response, "optimized_code"),
                getValue(response, "suggestions")
        );
    }

    private String getValue(Map<?, ?> response, String key) {
        Object value = response.get(key);
        return value == null || value.toString().isBlank()
                ? "No information provided."
                : value.toString().trim();
    }
}
