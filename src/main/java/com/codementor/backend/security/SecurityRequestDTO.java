package com.codementor.backend.security;

public class SecurityRequestDTO {

    private String code;
    private String language;

    public SecurityRequestDTO() {
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
