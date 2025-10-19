package com.example.demo.stats;

import java.util.List;

public class UserGroupComparisonDto {
    private List<EmotionAverage> userAverages;
    private List<EmotionAverage> groupAverages;

    public UserGroupComparisonDto(List<EmotionAverage> userAverages, List<EmotionAverage> groupAverages) {
        this.userAverages = userAverages;
        this.groupAverages = groupAverages;
    }

    // Getters and Setters
    public List<EmotionAverage> getUserAverages() {
        return userAverages;
    }

    public void setUserAverages(List<EmotionAverage> userAverages) {
        this.userAverages = userAverages;
    }

    public List<EmotionAverage> getGroupAverages() {
        return groupAverages;
    }

    public void setGroupAverages(List<EmotionAverage> groupAverages) {
        this.groupAverages = groupAverages;
    }
}
