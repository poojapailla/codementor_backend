package com.codementor.backend.tutor;

public class TutorRequestDTO {

    private String code;
    private String language;
    private String skillLevel;
    private String question;

    public TutorRequestDTO() {
    }

    public TutorRequestDTO(
            String code,
            String language,
            String skillLevel,
            String question
    ) {
        this.code = code;
        this.language = language;
        this.skillLevel = skillLevel;
        this.question = question;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
