package com.codementor.backend.complexity;

public class ComplexityRequestDTO {

    private String code;
    private String language;

    public ComplexityRequestDTO() {
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
}
