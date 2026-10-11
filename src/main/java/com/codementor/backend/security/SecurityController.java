package com.codementor.backend.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(
            SecurityService securityService
    ) {
        this.securityService = securityService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<SecurityResponseDTO> analyzeSecurity(
            @RequestBody SecurityRequestDTO request
    ) {

        return ResponseEntity.ok(
                securityService.analyzeSecurity(request)
        );
    }
}
