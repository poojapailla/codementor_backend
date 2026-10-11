package com.codementor.backend.controller;

import com.codementor.backend.dto.LoginRequestDTO;
import com.codementor.backend.dto.LoginResponseDTO;
import com.codementor.backend.dto.UserRequestDTO;
import com.codementor.backend.dto.UserResponseDTO;
import com.codementor.backend.security.SecurityUtils;
import com.codementor.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(userService.registerUser(dto));
    }

    // Login an existing user and receive a JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(userService.loginUser(dto));
    }

    // Identity of the currently authenticated user (from the JWT)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me() {

        return ResponseEntity.ok(
                userService.getCurrentUser(SecurityUtils.currentUserId())
        );
    }
}