package com.example.demo.stats;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConsultationStatDto {
    private Long consultationId;
    private LocalDateTime consultationDate;
    private List<EmotionChangeDto> emotionChanges;
}
