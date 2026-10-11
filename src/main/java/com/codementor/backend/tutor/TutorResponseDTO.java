package com.codementor.backend.tutor;

public class TutorResponseDTO {

    private String explanation;
    private String stepByStepGuide;
    private String example;
    private String practiceTip;

    public TutorResponseDTO() {
    }

    public TutorResponseDTO(
            String explanation,
            String stepByStepGuide,
            String example,
            String practiceTip
    ) {
        this.explanation = explanation;
        this.stepByStepGuide = stepByStepGuide;
        this.example = example;
        this.practiceTip = practiceTip;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getStepByStepGuide() {
        return stepByStepGuide;
    }

    public void setStepByStepGuide(String stepByStepGuide) {
        this.stepByStepGuide = stepByStepGuide;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getPracticeTip() {
        return practiceTip;
    }

    public void setPracticeTip(String practiceTip) {
        this.practiceTip = practiceTip;
    }
}
