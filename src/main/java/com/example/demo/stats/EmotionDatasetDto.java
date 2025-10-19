package com.example.demo.stats;

import java.util.List;

public class EmotionDatasetDto {
    private String label;
    private List<Integer> data;
    // getter, setter
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public List<Integer> getData() { return data; }
    public void setData(List<Integer> data) { this.data = data; }
}

