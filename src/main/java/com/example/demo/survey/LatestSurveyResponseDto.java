package com.example.demo.survey;

import java.util.List;

public class LatestSurveyResponseDto {
    private List<EmotionScore> scores;
    private String comments;

    public LatestSurveyResponseDto(List<EmotionScore> scores, String comments) {
        this.scores = scores;
        this.comments = comments;
    }

    // Getters and Setters
    public List<EmotionScore> getScores() {
        return scores;
    }

    public void setScores(List<EmotionScore> scores) {
        this.scores = scores;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
