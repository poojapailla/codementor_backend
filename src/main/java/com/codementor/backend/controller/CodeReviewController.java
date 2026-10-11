package com.codementor.backend.controller;

import com.codementor.backend.dto.CodeReviewRequestDTO;
import com.codementor.backend.dto.CodeReviewResponseDTO;
import com.codementor.backend.security.SecurityUtils;
import com.codementor.backend.service.CodeReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code-reviews")
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    public CodeReviewController(
            CodeReviewService codeReviewService
    ) {
        this.codeReviewService = codeReviewService;
    }

    // Create AI review for the authenticated user's submission
    @PostMapping
    public ResponseEntity<CodeReviewResponseDTO> createReview(
            @RequestBody CodeReviewRequestDTO dto
    ) {

        return ResponseEntity.ok(
                codeReviewService.createReview(
                        dto,
                        SecurityUtils.currentUserId()
                )
        );
    }

    // Get review by review ID (only if owned by the authenticated user)
    @GetMapping("/{id}")
    public ResponseEntity<CodeReviewResponseDTO> getReview(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                codeReviewService.getReviewById(
                        id,
                        SecurityUtils.currentUserId()
                )
        );
    }

    // Get review for a submission the authenticated user owns
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<CodeReviewResponseDTO> getReviewBySubmission(
            @PathVariable Long submissionId
    ) {

        return ResponseEntity.ok(
                codeReviewService.getReviewBySubmissionId(
                        submissionId,
                        SecurityUtils.currentUserId()
                )
        );
    }
}