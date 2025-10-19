package com.example.demo.admin;

import com.example.demo.domain.Question;
import com.example.demo.domain.Survey;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CustomSurveyResponseDto {
    private Long id;
    private String title;
    private String createdBy;
    private LocalDateTime creationDate;
    private List<QuestionDto> questions;

    public CustomSurveyResponseDto(Survey survey) {
        this.id = survey.getId();
        this.title = survey.getTitle();
        if (survey.getCreatedBy() != null) {
            this.createdBy = survey.getCreatedBy().getName();
        }
        this.creationDate = survey.getCreationDate();
        this.questions = survey.getQuestions().stream()
                .map(question -> {
                    QuestionDto dto = new QuestionDto();
                    dto.setText(question.getText());
                    dto.setScoreRange(List.of(question.getMinScore(), question.getMaxScore()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
