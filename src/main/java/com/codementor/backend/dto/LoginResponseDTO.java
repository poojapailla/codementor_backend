package com.codementor.backend.dto;

public class LoginResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String message;
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(Long id, String name, String email, String role, String message, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}