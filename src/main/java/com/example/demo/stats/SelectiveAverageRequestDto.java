package com.example.demo.stats;

import java.util.List;

public class SelectiveAverageRequestDto {
    private List<String> emotionKeys;
    private PeriodDto period;
    // getter, setter
    public List<String> getEmotionKeys() { return emotionKeys; }
    public void setEmotionKeys(List<String> emotionKeys) { this.emotionKeys = emotionKeys; }
    public PeriodDto getPeriod() { return period; }
    public void setPeriod(PeriodDto period) { this.period = period; }
}

