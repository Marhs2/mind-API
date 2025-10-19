package com.example.demo.survey;

public class EmotionScore {
    private String emotion;
    private int score;

    public EmotionScore(String emotion, int score) {
        this.emotion = emotion;
        this.score = score;
    }

    // Getters and Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
