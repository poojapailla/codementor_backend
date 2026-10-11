package com.codementor.backend.security;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityService {

    public SecurityResponseDTO analyzeSecurity(
            SecurityRequestDTO request
    ) {

        String code = request.getCode();

        List<SecurityResponseDTO.SecurityIssue> issues =
                new ArrayList<>();

        String lowerCode = code.toLowerCase();

        // Hardcoded password or secret detection
        if (lowerCode.contains("password")
                || lowerCode.contains("secret")
                || lowerCode.contains("apikey")
                || lowerCode.contains("api_key")) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "HIGH",
                            "Possible hardcoded credential",
                            "The code appears to contain a password, secret, "
                                    + "API key, or another credential directly "
                                    + "inside the source code.",
                            "Store credentials in environment variables or "
                                    + "a secure secrets manager instead of "
                                    + "hardcoding them."
                    )
            );
        }

        // SQL injection pattern detection
        if (lowerCode.contains("select ")
                && (lowerCode.contains("+")
                || lowerCode.contains("concat")
                || lowerCode.contains("string.format"))) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "HIGH",
                            "Possible SQL injection",
                            "SQL appears to be constructed by combining "
                                    + "query strings with other values.",
                            "Use prepared statements or parameterized "
                                    + "queries instead of directly building "
                                    + "SQL strings."
                    )
            );
        }

        // Command injection pattern detection
        if (lowerCode.contains("runtime.getruntime().exec")
                || lowerCode.contains("processbuilder")) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "HIGH",
                            "Potential command injection",
                            "The code executes operating-system commands. "
                                    + "Using untrusted user input in these "
                                    + "commands can allow command injection.",
                            "Validate and restrict user input and avoid "
                                    + "executing commands constructed from "
                                    + "untrusted data."
                    )
            );
        }

        // Unsafe file path detection
        if (lowerCode.contains("fileinputstream")
                || lowerCode.contains("fileoutputstream")
                || lowerCode.contains("new file(")) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "MEDIUM",
                            "File operation requires validation",
                            "The code performs file operations. If file paths "
                                    + "come from users, path traversal may "
                                    + "be possible.",
                            "Validate file paths and restrict access to an "
                                    + "approved directory."
                    )
            );
        }

        // Unsafe deserialization detection
        if (lowerCode.contains("objectinputstream")) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "HIGH",
                            "Potential unsafe deserialization",
                            "ObjectInputStream can deserialize untrusted "
                                    + "objects and may introduce security "
                                    + "risks.",
                            "Avoid deserializing untrusted data and use a "
                                    + "safe serialization format such as JSON."
                    )
            );
        }

        // Weak cryptography detection
        if (lowerCode.contains("md5")
                || lowerCode.contains("sha-1")
                || lowerCode.contains("sha1")) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "MEDIUM",
                            "Weak cryptographic algorithm",
                            "The code appears to use MD5 or SHA-1, which "
                                    + "are not recommended for modern security "
                                    + "applications.",
                            "Use modern cryptographic algorithms such as "
                                    + "SHA-256 where appropriate, or use a "
                                    + "password hashing algorithm such as "
                                    + "bcrypt, scrypt, or Argon2 for passwords."
                    )
            );
        }

        // Empty input validation
        if (code == null || code.trim().isEmpty()) {

            issues.add(
                    new SecurityResponseDTO.SecurityIssue(
                            "LOW",
                            "Empty source code",
                            "No source code was provided for security analysis.",
                            "Submit valid source code for a meaningful "
                                    + "security analysis."
                    )
            );
        }

        int securityScore = calculateSecurityScore(issues);

        String summary;

        if (issues.isEmpty()) {

            summary =
                    "No obvious security vulnerabilities were detected "
                            + "by the current security rules.";

        } else {

            summary =
                    issues.size()
                            + " potential security issue(s) were detected. "
                            + "Review the recommendations before using the "
                            + "code in production.";
        }

        return new SecurityResponseDTO(
                securityScore,
                issues,
                summary
        );
    }

    private int calculateSecurityScore(
            List<SecurityResponseDTO.SecurityIssue> issues
    ) {

        int score = 100;

        for (SecurityResponseDTO.SecurityIssue issue : issues) {

            switch (issue.getSeverity()) {

                case "HIGH":
                    score -= 25;
                    break;

                case "MEDIUM":
                    score -= 15;
                    break;

                case "LOW":
                    score -= 5;
                    break;

                default:
                    break;
            }
        }

        return Math.max(0, score);
    }
}
