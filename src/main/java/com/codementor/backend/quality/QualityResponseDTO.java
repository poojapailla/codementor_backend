package com.codementor.backend.quality;

import java.util.List;

public class QualityResponseDTO {

    private int qualityScore;
    private String overallRating;
    private List<QualityIssue> issues;
    private String summary;

    public QualityResponseDTO() {
    }

    public QualityResponseDTO(
            int qualityScore,
            String overallRating,
            List<QualityIssue> issues,
            String summary
    ) {
        this.qualityScore = qualityScore;
        this.overallRating = overallRating;
        this.issues = issues;
        this.summary = summary;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public List<QualityIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<QualityIssue> issues) {
        this.issues = issues;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class QualityIssue {

        private String category;
        private String severity;
        private String description;
        private String recommendation;

        public QualityIssue() {
        }

        public QualityIssue(
                String category,
                String severity,
                String description,
                String recommendation
        ) {
            this.category = category;
            this.severity = severity;
            this.description = description;
            this.recommendation = recommendation;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}
