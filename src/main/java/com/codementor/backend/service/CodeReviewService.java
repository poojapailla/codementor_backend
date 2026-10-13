package com.codementor.backend.service;

import com.codementor.backend.dto.CodeReviewRequestDTO;
import com.codementor.backend.dto.CodeReviewResponseDTO;
import com.codementor.backend.model.CodeReview;
import com.codementor.backend.model.CodeSubmission;
import com.codementor.backend.repository.CodeReviewRepository;
import com.codementor.backend.repository.CodeSubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class CodeReviewService {

    private final CodeReviewRepository codeReviewRepository;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final AIService aiService;

    public CodeReviewService(
            CodeReviewRepository codeReviewRepository,
            CodeSubmissionRepository codeSubmissionRepository,
            AIService aiService
    ) {
        this.codeReviewRepository = codeReviewRepository;
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.aiService = aiService;
    }

    public CodeReviewResponseDTO createReview(
            CodeReviewRequestDTO dto,
            Long currentUserId
    ) {

        CodeSubmission submission = findOwnedSubmission(
                dto.getSubmissionId(),
                currentUserId
        );

        if (codeReviewRepository.existsBySubmission(submission)) {
            throw new RuntimeException(
                    "This code submission has already been reviewed"
            );
        }

        CodeReviewResponseDTO aiResponse =
                aiService.reviewCode(submission);

        CodeReview review = new CodeReview();

        review.setSubmission(submission);
        review.setBugAnalysis(aiResponse.getBugAnalysis());
        review.setExplanation(aiResponse.getExplanation());
        review.setSuggestions(aiResponse.getSuggestions());
        review.setOptimizedCode(aiResponse.getOptimizedCode());
        review.setQualityScore(aiResponse.getQualityScore());

        CodeReview savedReview =
                codeReviewRepository.save(review);

        return new CodeReviewResponseDTO(
                savedReview.getId(),
                savedReview.getSubmission().getId(),
                savedReview.getBugAnalysis(),
                savedReview.getExplanation(),
                savedReview.getSuggestions(),
                savedReview.getOptimizedCode(),
                savedReview.getQualityScore(),
                savedReview.getCreatedAt()
        );
    }

    public CodeReviewResponseDTO getReviewById(
            Long id,
            Long currentUserId
    ) {

        CodeReview review =
                codeReviewRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Code review not found"
                                ));

        verifyOwnership(review.getSubmission(), currentUserId);

        return convertToResponse(review);
    }

    public CodeReviewResponseDTO getReviewBySubmissionId(
            Long submissionId,
            Long currentUserId
    ) {

        CodeSubmission submission = findOwnedSubmission(
                submissionId,
                currentUserId
        );

        CodeReview review =
                codeReviewRepository.findBySubmission(submission)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Review not found for this submission"
                                ));

        return convertToResponse(review);
    }

    private CodeSubmission findOwnedSubmission(
            Long submissionId,
            Long currentUserId
    ) {
        CodeSubmission submission =
                codeSubmissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Code submission not found"
                                ));

        verifyOwnership(submission, currentUserId);

        return submission;
    }

    private void verifyOwnership(
            CodeSubmission submission,
            Long currentUserId
    ) {
        if (!submission.getUser().getId().equals(currentUserId)) {
            // Do not reveal that another user's submission exists.
            throw new RuntimeException("Code submission not found");
        }
    }

    private CodeReviewResponseDTO convertToResponse(
            CodeReview review
    ) {

        return new CodeReviewResponseDTO(
                review.getId(),
                review.getSubmission().getId(),
                review.getBugAnalysis(),
                review.getExplanation(),
                review.getSuggestions(),
                review.getOptimizedCode(),
                review.getQualityScore(),
                review.getCreatedAt()
        );
    }
}