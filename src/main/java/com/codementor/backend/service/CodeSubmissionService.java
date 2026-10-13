package com.codementor.backend.service;

import com.codementor.backend.dto.CodeSubmissionRequestDTO;
import com.codementor.backend.dto.CodeSubmissionResponseDTO;
import com.codementor.backend.model.CodeSubmission;
import com.codementor.backend.model.User;
import com.codementor.backend.repository.CodeSubmissionRepository;
import com.codementor.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeSubmissionService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final UserRepository userRepository;

    public CodeSubmissionService(
            CodeSubmissionRepository codeSubmissionRepository,
            UserRepository userRepository
    ) {
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.userRepository = userRepository;
    }

    // Submit new code. The owner is the authenticated user, never a
    // client-supplied ID.
    public CodeSubmissionResponseDTO submitCode(
            CodeSubmissionRequestDTO dto,
            Long currentUserId
    ) {

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        CodeSubmission submission = new CodeSubmission();

        submission.setUser(user);
        submission.setTitle(dto.getTitle());
        submission.setLanguage(dto.getLanguage());
        submission.setCode(dto.getCode());
        submission.setProblemDescription(
                dto.getProblemDescription()
        );

        CodeSubmission savedSubmission =
                codeSubmissionRepository.save(submission);

        return convertToResponse(savedSubmission);
    }

    // Get a submission by ID, only if it belongs to the authenticated user.
    public CodeSubmissionResponseDTO getSubmissionById(
            Long id,
            Long currentUserId
    ) {

        CodeSubmission submission = findOwnedSubmission(id, currentUserId);

        return convertToResponse(submission);
    }

    // Get all submissions of the authenticated user.
    public List<CodeSubmissionResponseDTO> getUserSubmissions(
            Long currentUserId
    ) {

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return codeSubmissionRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
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

        if (!submission.getUser().getId().equals(currentUserId)) {
            // Do not reveal that another user's submission exists.
            throw new RuntimeException("Code submission not found");
        }

        return submission;
    }

    // Convert Entity to Response DTO
    private CodeSubmissionResponseDTO convertToResponse(
            CodeSubmission submission
    ) {

        return new CodeSubmissionResponseDTO(
                submission.getId(),
                submission.getUser().getId(),
                submission.getTitle(),
                submission.getLanguage(),
                submission.getCode(),
                submission.getProblemDescription(),
                submission.getCreatedAt()
        );
    }
}