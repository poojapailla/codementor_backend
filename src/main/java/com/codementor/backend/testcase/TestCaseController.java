package com.codementor.backend.testcase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController(
            TestCaseService testCaseService
    ) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TestCaseResponseDTO> generateTestCases(
            @RequestBody TestCaseRequestDTO request
    ) {

        return ResponseEntity.ok(
                testCaseService.generateTestCases(request)
        );
    }
}
