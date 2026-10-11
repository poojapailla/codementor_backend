package com.codementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CodeReviewResponseDTO {

    private Long id;
    private Long submissionId;

    @JsonProperty("bug_analysis")
    private String bugAnalysis;

    private String explanation;

    private String suggestions;

    @JsonProperty("optimized_code")
    private String optimizedCode;

    @JsonProperty("quality_score")
    private Integer qualityScore;

    private LocalDateTime createdAt;

    public CodeReviewResponseDTO() {
    }

    public CodeReviewResponseDTO(
            Long id,
            Long submissionId,
            String bugAnalysis,
            String explanation,
            String suggestions,
            String optimizedCode,
            Integer qualityScore,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.submissionId = submissionId;
        this.bugAnalysis = bugAnalysis;
        this.explanation = explanation;
        this.suggestions = suggestions;
        this.optimizedCode = optimizedCode;
        this.qualityScore = qualityScore;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public String getBugAnalysis() {
        return bugAnalysis;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public String getOptimizedCode() {
        return optimizedCode;
    }

    public Integer getQualityScore() {
        return qualityScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}