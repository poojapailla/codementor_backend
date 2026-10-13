package com.codementor.backend.dto;

public class CodeReviewRequestDTO {

    private Long submissionId;

    public CodeReviewRequestDTO() {
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }
}