package com.codementor.backend.repository;

import com.codementor.backend.model.CodeReview;
import com.codementor.backend.model.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeReviewRepository
        extends JpaRepository<CodeReview, Long> {

    Optional<CodeReview> findBySubmission(CodeSubmission submission);

    boolean existsBySubmission(CodeSubmission submission);
}