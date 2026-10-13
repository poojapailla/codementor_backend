package com.codementor.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_reviews")
public class CodeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private CodeSubmission submission;

    @Column(columnDefinition = "TEXT")
    private String bugAnalysis;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String optimizedCode;

    @Column
    private Integer qualityScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CodeReview() {
    }

    public CodeReview(
            CodeSubmission submission,
            String bugAnalysis,
            String explanation,
            String suggestions,
            String optimizedCode,
            Integer qualityScore
    ) {
        this.submission = submission;
        this.bugAnalysis = bugAnalysis;
        this.explanation = explanation;
        this.suggestions = suggestions;
        this.optimizedCode = optimizedCode;
        this.qualityScore = qualityScore;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public CodeSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(CodeSubmission submission) {
        this.submission = submission;
    }

    public String getBugAnalysis() {
        return bugAnalysis;
    }

    public void setBugAnalysis(String bugAnalysis) {
        this.bugAnalysis = bugAnalysis;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getOptimizedCode() {
        return optimizedCode;
    }

    public void setOptimizedCode(String optimizedCode) {
        this.optimizedCode = optimizedCode;
    }

    public Integer getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Integer qualityScore) {
        this.qualityScore = qualityScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}