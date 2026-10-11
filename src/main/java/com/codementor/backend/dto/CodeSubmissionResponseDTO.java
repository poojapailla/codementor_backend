package com.codementor.backend.dto;

import java.time.LocalDateTime;

public class CodeSubmissionResponseDTO {

    private Long id;
    private Long userId;
    private String title;
    private String language;
    private String code;
    private String problemDescription;
    private LocalDateTime createdAt;

    public CodeSubmissionResponseDTO() {
    }

    public CodeSubmissionResponseDTO(
            Long id,
            Long userId,
            String title,
            String language,
            String code,
            String problemDescription,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.language = language;
        this.code = code;
        this.problemDescription = problemDescription;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}