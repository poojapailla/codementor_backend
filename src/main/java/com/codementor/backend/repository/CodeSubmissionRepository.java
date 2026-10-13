package com.codementor.backend.repository;

import com.codementor.backend.model.CodeSubmission;
import com.codementor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeSubmissionRepository
        extends JpaRepository<CodeSubmission, Long> {

    List<CodeSubmission> findByUser(User user);

    List<CodeSubmission> findByUserOrderByCreatedAtDesc(User user);
}