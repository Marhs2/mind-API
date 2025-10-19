package com.example.demo.admin;

import com.example.demo.domain.SurveyResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminSurveyResponseDto {

    private Long id;
    private String surveyType;
    private Integer depression;
    private Integer anxiety;
    private Integer joy;
    private Integer anger;
    private Integer fatigue;
    private Integer loneliness;
    private Integer stability;
    private Integer selfSatisfaction;
    private String comments;
    private LocalDateTime submissionDate;
    private Long userId;
    private String userName;

    public AdminSurveyResponseDto(SurveyResult surveyResult) {
        this.id = surveyResult.getId();
        this.surveyType = surveyResult.getSurveyType();
        this.depression = surveyResult.getDepression();
        this.anxiety = surveyResult.getAnxiety();
        this.joy = surveyResult.getJoy();
        this.anger = surveyResult.getAnger();
        this.fatigue = surveyResult.getFatigue();
        this.loneliness = surveyResult.getLoneliness();
        this.stability = surveyResult.getStability();
        this.selfSatisfaction = surveyResult.getSelfSatisfaction();
        this.comments = surveyResult.getComments();
        this.submissionDate = surveyResult.getSubmissionDate();
        if (surveyResult.getUser() != null) {
            this.userId = surveyResult.getUser().getId();
            this.userName = surveyResult.getUser().getName();
        }
    }
}
