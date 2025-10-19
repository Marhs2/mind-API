package com.example.demo.stats;

import java.util.List;
import java.util.Map;

public class EmotionDistributionDto {

    private String emotionName;
    private List<String> labels; // "0", "1", ..., "10"
    private List<Integer> data; // count for each score

    public EmotionDistributionDto(String emotionName, List<String> labels, List<Integer> data) {
        this.emotionName = emotionName;
        this.labels = labels;
        this.data = data;
    }

    public String getEmotionName() {
        return emotionName;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
