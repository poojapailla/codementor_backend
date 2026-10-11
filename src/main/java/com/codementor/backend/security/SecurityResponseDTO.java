package com.codementor.backend.security;

import java.util.List;

public class SecurityResponseDTO {

    private int securityScore;
    private List<SecurityIssue> issues;
    private String summary;

    public SecurityResponseDTO() {
    }

    public SecurityResponseDTO(
            int securityScore,
            List<SecurityIssue> issues,
            String summary
    ) {
        this.securityScore = securityScore;
        this.issues = issues;
        this.summary = summary;
    }

    public int getSecurityScore() {
        return securityScore;
    }

    public void setSecurityScore(int securityScore) {
        this.securityScore = securityScore;
    }

    public List<SecurityIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<SecurityIssue> issues) {
        this.issues = issues;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class SecurityIssue {

        private String severity;
        private String issue;
        private String explanation;
        private String recommendation;

        public SecurityIssue() {
        }

        public SecurityIssue(
                String severity,
                String issue,
                String explanation,
                String recommendation
        ) {
            this.severity = severity;
            this.issue = issue;
            this.explanation = explanation;
            this.recommendation = recommendation;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}
