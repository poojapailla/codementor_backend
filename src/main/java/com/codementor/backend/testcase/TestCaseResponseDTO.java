package com.codementor.backend.testcase;

import java.util.List;

public class TestCaseResponseDTO {

    private List<TestCase> testCases;
    private String notes;

    public TestCaseResponseDTO() {
    }

    public TestCaseResponseDTO(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public TestCaseResponseDTO(List<TestCase> testCases, String notes) {
        this.testCases = testCases;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public static class TestCase {

        private String name;
        private String type;
        private String input;
        private String expectedOutput;
        private String explanation;

        public TestCase() {
        }

        public TestCase(
                String type,
                String input,
                String expectedOutput,
                String explanation
        ) {
            this.type = type;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.explanation = explanation;
        }

        public TestCase(
                String name,
                String type,
                String input,
                String expectedOutput,
                String explanation
        ) {
            this.name = name;
            this.type = type;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.explanation = explanation;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
}
