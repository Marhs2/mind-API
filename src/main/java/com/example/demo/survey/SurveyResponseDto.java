package com.example.demo.survey;

public class SurveyResponseDto {
    private String message;
    private String surveyId;
    public SurveyResponseDto(String message, String surveyId) {
        this.message = message;
        this.surveyId = surveyId;
    }
    // getter, setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }
}

