package com.example.demo.stats;

import java.util.List;

public class PersonalAveragesResponseDto {
    private List<EmotionAverage> averages;

    public PersonalAveragesResponseDto(List<EmotionAverage> averages) {
        this.averages = averages;
    }

    // Getters and Setters
    public List<EmotionAverage> getAverages() {
        return averages;
    }

    public void setAverages(List<EmotionAverage> averages) {
        this.averages = averages;
    }
}
