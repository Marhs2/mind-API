package com.example.demo.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmotionChangeDto {
    private String emotionName;
    private int beforeScore;
    private int afterScore;
    private int change;
}
