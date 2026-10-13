package com.codementor.backend.complexity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complexity")
public class ComplexityController {

    private final ComplexityService complexityService;

    public ComplexityController(
            ComplexityService complexityService
    ) {
        this.complexityService = complexityService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ComplexityResponseDTO> analyzeComplexity(
            @RequestBody ComplexityRequestDTO request
    ) {

        return ResponseEntity.ok(
                complexityService.analyzeComplexity(request)
        );
    }
}
