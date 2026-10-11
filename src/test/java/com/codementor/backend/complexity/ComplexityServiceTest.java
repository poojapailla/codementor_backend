package com.codementor.backend.complexity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ComplexityServiceTest {

    private final ComplexityService service = new ComplexityService();

    @Test
    void rejectsBlankCode() {
        ComplexityRequestDTO request = new ComplexityRequestDTO();
        request.setCode("   ");

        assertThrows(IllegalArgumentException.class,
                () -> service.analyzeComplexity(request));
    }

    @Test
    void estimatesLinearComplexityForSingleLoop() {
        ComplexityRequestDTO request = new ComplexityRequestDTO();
        request.setCode(
                "public class Main {\n"
                + "  public static void main(String[] a) {\n"
                + "    for (int i = 0; i < 10; i++) { System.out.println(i); }\n"
                + "  }\n"
                + "}");

        ComplexityResponseDTO response = service.analyzeComplexity(request);

        assertTrue(response.getTimeComplexity().startsWith("O(n)"));
        assertTrue(response.getSpaceComplexity().startsWith("O(1)"));
    }

    @Test
    void detectsRecursiveMethod() {
        ComplexityRequestDTO request = new ComplexityRequestDTO();
        request.setCode(
                "public class Main {\n"
                + "  static int fact(int n) {\n"
                + "    if (n <= 1) return 1;\n"
                + "    return n * fact(n - 1);\n"
                + "  }\n"
                + "}");

        ComplexityResponseDTO response = service.analyzeComplexity(request);

        assertTrue(response.getTimeComplexity().contains("recursion"));
    }

    @Test
    void returnsEstimatedMarkerOnAllResults() {
        ComplexityRequestDTO request = new ComplexityRequestDTO();
        request.setCode("print('hello')");

        ComplexityResponseDTO response = service.analyzeComplexity(request);

        assertTrue(response.getTimeComplexity().contains("estimated"));
        assertEquals("O(1) — estimated", response.getSpaceComplexity());
    }
}
