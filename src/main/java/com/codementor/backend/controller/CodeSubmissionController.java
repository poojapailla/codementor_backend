package com.codementor.backend.controller;

import com.codementor.backend.dto.CodeSubmissionRequestDTO;
import com.codementor.backend.dto.CodeSubmissionResponseDTO;
import com.codementor.backend.security.SecurityUtils;
import com.codementor.backend.service.CodeSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/code-submissions")
public class CodeSubmissionController {

    private final CodeSubmissionService codeSubmissionService;

    public CodeSubmissionController(
            CodeSubmissionService codeSubmissionService
    ) {
        this.codeSubmissionService = codeSubmissionService;
    }

    // Submit new code (owner comes from the validated JWT)
    @PostMapping
    public ResponseEntity<CodeSubmissionResponseDTO> submitCode(
            @RequestBody CodeSubmissionRequestDTO dto
    ) {

        return ResponseEntity.ok(
                codeSubmissionService.submitCode(
                        dto,
                        SecurityUtils.currentUserId()
                )
        );
    }

    // Get a submission the authenticated user owns
    @GetMapping("/{id}")
    public ResponseEntity<CodeSubmissionResponseDTO> getSubmission(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                codeSubmissionService.getSubmissionById(
                        id,
                        SecurityUtils.currentUserId()
                )
        );
    }

    // Get all submissions of the authenticated user
    @GetMapping("/me")
    public ResponseEntity<List<CodeSubmissionResponseDTO>> getMySubmissions() {

        return ResponseEntity.ok(
                codeSubmissionService.getUserSubmissions(
                        SecurityUtils.currentUserId()
                )
        );
    }
}