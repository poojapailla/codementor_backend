package com.codementor.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_submissions")
public class CodeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String problemDescription;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CodeSubmission() {
    }

    public CodeSubmission(
            User user,
            String title,
            String language,
            String code,
            String problemDescription
    ) {
        this.user = user;
        this.title = title;
        this.language = language;
        this.code = code;
        this.problemDescription = problemDescription;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}