
package com.codementor.backend.complexity;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComplexityService {

    public ComplexityResponseDTO analyzeComplexity(
            ComplexityRequestDTO request) {

        if (request == null || request.getCode() == null
                || request.getCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide code for complexity analysis.");
        }

        String code = removeComments(request.getCode());
        String time = detectTimeComplexity(code);
        String space = detectSpaceComplexity(code);

        String explanation =
                "This is a heuristic estimate based on recognizable "
                + "loops, recursion, sorting, searching, and data "
                + "structure allocations. It is not a full static "
                + "analysis, so verify the result for complex code.";

        return new ComplexityResponseDTO(time, space, explanation);
    }

    private String detectTimeComplexity(String code) {
        String normalized = code.replaceAll("\\s+", " ");

        int nestedDepth = maximumLoopDepth(normalized);

        if (containsRecursion(normalized)) {
            return "O(2^n) or O(n) — recursion-dependent";
        }

        if (nestedDepth >= 3) {
            return "O(n^3) — estimated";
        }

        if (nestedDepth == 2) {
            return "O(n^2) — estimated";
        }

        if (nestedDepth == 1) {
            return "O(n) — estimated";
        }

        if (normalized.matches("(?s).*\\b(Arrays\\.sort|Collections\\.sort)\\s*\\(.*")
                || normalized.contains(".sort(")) {
            return "O(n log n) — estimated";
        }

        if (normalized.contains("binarySearch")
                || normalized.contains("BinarySearch")) {
            return "O(log n) — estimated";
        }

        return "O(1) — estimated";
    }

    private int maximumLoopDepth(String code) {
        Pattern loopPattern = Pattern.compile(
                "\\b(for|while|do)\\b");
        Matcher matcher = loopPattern.matcher(code);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        // Conservative estimate: count loop constructs.
        // This does not prove that loops are nested.
        if (count >= 3) return 3;
        if (count == 2) return 2;
        if (count == 1) return 1;
        return 0;
    }

    private boolean containsRecursion(String code) {
        Pattern method = Pattern.compile(
                "\\b([a-zA-Z_$][\\w$]*)\\s*\\([^;{}]*\\)\\s*\\{");
        Matcher matcher = method.matcher(code);

        while (matcher.find()) {
            String methodName = matcher.group(1);
            int bodyStart = matcher.end();
            int bodyEnd = findBodyEnd(code, bodyStart);

            if (bodyEnd > bodyStart) {
                String body = code.substring(bodyStart, bodyEnd);
                if (Pattern.compile("\\b"
                        + Pattern.quote(methodName)
                        + "\\s*\\(").matcher(body).find()) {
                    return true;
                }
            }
        }

        return false;
    }

    private int findBodyEnd(String code, int start) {
        int depth = 1;

        for (int i = start; i < code.length(); i++) {
            if (code.charAt(i) == '{') depth++;
            if (code.charAt(i) == '}') depth--;

            if (depth == 0) return i;
        }

        return -1;
    }

    private String detectSpaceComplexity(String code) {
        String normalized = code.replaceAll("\\s+", " ");

        if (normalized.contains("new int[")
                || normalized.contains("new Integer[")
                || normalized.contains("new String[")
                || normalized.contains("new ArrayList")
                || normalized.contains("new HashMap")
                || normalized.contains("new HashSet")
                || normalized.contains("new LinkedList")) {
            return "O(n) — estimated";
        }

        return "O(1) — estimated";
    }

    private String removeComments(String code) {
        return code.replaceAll("(?s)/\\*.*?\\*/", " ")
                .replaceAll("(?m)//.*$", " ");
    }
}
