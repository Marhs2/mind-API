package com.example.demo.survey;

public class ConsultationSummaryDto {
    private String consultationId;
    private String status;
    private String createdDate;
    private boolean hasBeforeSurvey;
    private boolean hasAfterSurvey;
    // getter, setter
    public String getConsultationId() { return consultationId; }
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public boolean isHasBeforeSurvey() { return hasBeforeSurvey; }
    public void setHasBeforeSurvey(boolean hasBeforeSurvey) { this.hasBeforeSurvey = hasBeforeSurvey; }
    public boolean isHasAfterSurvey() { return hasAfterSurvey; }
    public void setHasAfterSurvey(boolean hasAfterSurvey) { this.hasAfterSurvey = hasAfterSurvey; }
}

