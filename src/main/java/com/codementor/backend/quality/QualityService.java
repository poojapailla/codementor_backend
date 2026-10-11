package com.codementor.backend.quality;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QualityService {

    public QualityResponseDTO analyzeQuality(
            QualityRequestDTO request
    ) {

        List<QualityResponseDTO.QualityIssue> issues =
                new ArrayList<>();

        String code = request.getCode();

        if (code == null || code.trim().isEmpty()) {
            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Input Validation",
                            "HIGH",
                            "No source code was provided.",
                            "Submit valid source code for analysis."
                    )
            );

            return new QualityResponseDTO(
                    0,
                    "Poor",
                    issues,
                    "Quality analysis could not be performed because the code is empty."
            );
        }

        String[] lines = code.split("\\R", -1);
        int score = 100;

        // Check for long lines
        int longLineCount = 0;

        for (String line : lines) {
            if (line.length() > 120) {
                longLineCount++;
            }
        }

        if (longLineCount > 0) {
            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Readability",
                            "MEDIUM",
                            longLineCount + " line(s) exceed 120 characters.",
                            "Break long statements into smaller, readable lines."
                    )
            );

            score -= Math.min(15, longLineCount * 3);
        }

        // Check for TODO or FIXME markers
        String upperCode = code.toUpperCase();

        if (upperCode.contains("TODO")
                || upperCode.contains("FIXME")) {

            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Maintainability",
                            "LOW",
                            "The code contains TODO or FIXME markers.",
                            "Resolve pending tasks or document them in a tracked issue."
                    )
            );

            score -= 5;
        }

        // Check for very long methods using a basic brace counter
        int braceDepth = 0;
        int methodBraceDepth = -1;
        int methodLineCount = 0;
        int longMethodCount = 0;

        for (String line : lines) {

            String trimmed = line.trim();

            if (methodBraceDepth != -1) {
                methodLineCount++;
            }

            if (methodBraceDepth == -1
                    && trimmed.matches(
                    ".*\\b(public|private|protected)\\b.*\\(.*\\).*\\{?\\s*")) {

                methodBraceDepth = braceDepth;
                methodLineCount = 1;
            }

            for (char character : line.toCharArray()) {
                if (character == '{') {
                    braceDepth++;
                } else if (character == '}') {
                    braceDepth--;
                }
            }

            if (methodBraceDepth != -1
                    && braceDepth <= methodBraceDepth) {

                if (methodLineCount > 40) {
                    longMethodCount++;
                }

                methodBraceDepth = -1;
                methodLineCount = 0;
            }
        }

        if (longMethodCount > 0) {
            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Maintainability",
                            "MEDIUM",
                            longMethodCount + " potentially long method(s) were detected.",
                            "Split large methods into smaller methods with clear responsibilities."
                    )
            );

            score -= Math.min(20, longMethodCount * 10);
        }

        // Check for magic numbers
        if (code.matches("(?s).*\\b(86400|3600|1000|9999)\\b.*")) {
            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Readability",
                            "LOW",
                            "The code contains numeric values that may represent unexplained constants.",
                            "Replace unexplained numeric values with clearly named constants where appropriate."
                    )
            );

            score -= 5;
        }

        // Check for debug output
        if (code.contains("System.out.println")
                || code.contains("printStackTrace()")) {

            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Logging",
                            "LOW",
                            "Console output or stack-trace printing was detected.",
                            "Use an appropriate logging framework for production applications."
                    )
            );

            score -= 5;
        }

        // Check for very large source files
        if (lines.length > 300) {
            issues.add(
                    new QualityResponseDTO.QualityIssue(
                            "Maintainability",
                            "MEDIUM",
                            "The source file contains more than 300 lines.",
                            "Consider separating responsibilities into smaller classes or modules."
                    )
            );

            score -= 10;
        }

        score = Math.max(0, Math.min(100, score));

        String overallRating;

        if (score >= 90) {
            overallRating = "Excellent";
        } else if (score >= 75) {
            overallRating = "Good";
        } else if (score >= 60) {
            overallRating = "Fair";
        } else {
            overallRating = "Needs Improvement";
        }

        String summary;

        if (issues.isEmpty()) {
            summary =
                    "No issues covered by the current quality rules were detected. "
                    + "This does not guarantee that the code is bug-free or well-designed.";
        } else {
            summary =
                    issues.size()
                    + " potential quality issue(s) were detected. "
                    + "The score is a preliminary rule-based estimate, not a full code audit.";
        }

        return new QualityResponseDTO(
                score,
                overallRating,
                issues,
                summary
        );
    }
}
