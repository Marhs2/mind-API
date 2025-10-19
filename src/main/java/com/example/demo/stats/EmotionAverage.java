package com.example.demo.stats;

public class EmotionAverage {
    private String emotion;
    private double averageScore;

    public EmotionAverage(String emotion, double averageScore) {
        this.emotion = emotion;
        this.averageScore = averageScore;
    }

    // Getters and Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}
