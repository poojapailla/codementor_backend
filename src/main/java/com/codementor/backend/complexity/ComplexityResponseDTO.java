package com.codementor.backend.complexity;

public class ComplexityResponseDTO {

    private String timeComplexity;
    private String spaceComplexity;
    private String explanation;

    public ComplexityResponseDTO() {
    }

    public ComplexityResponseDTO(
            String timeComplexity,
            String spaceComplexity,
            String explanation
    ) {
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.explanation = explanation;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public String getExplanation() {
        return explanation;
    }
}
