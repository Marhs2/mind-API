package com.example.demo.stats;

import java.util.List;

public class EmotionTrendResponseDto {
    private List<String> labels;
    private List<EmotionDatasetDto> datasets;
    // getter, setter
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<EmotionDatasetDto> getDatasets() { return datasets; }
    public void setDatasets(List<EmotionDatasetDto> datasets) { this.datasets = datasets; }
}

