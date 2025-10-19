package com.example.demo.survey;

public class ConsultationDetailDto {
    private String consultationId;
    private SurveyRequestDto before;
    private SurveyRequestDto after;
    // getter, setter
    public String getConsultationId() { return consultationId; }
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public SurveyRequestDto getBefore() { return before; }
    public void setBefore(SurveyRequestDto before) { this.before = before; }
    public SurveyRequestDto getAfter() { return after; }
    public void setAfter(SurveyRequestDto after) { this.after = after; }
}

