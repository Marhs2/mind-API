package com.example.demo.survey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyRequestDto {

    private String consultationId; // Can be null if not associated with a consultation

    @Pattern(regexp = "^(Before|After)$", message = "설문 타입은 'Before' 또는 'After'만 입력 가능합니다.")
    private String surveyType; // Before | After

    @Min(value = 1, message = "우울감 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "우울감 점수는 10점 이하여야 합니다.")
    private int depression;

    @Min(value = 1, message = "불안감 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "불안감 점수는 10점 이하여야 합니다.")
    private int anxiety;

    @Min(value = 1, message = "기쁨 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "기쁨 점수는 10점 이하여야 합니다.")
    private int joy;

    @Min(value = 1, message = "분노 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "분노 점수는 10점 이하여야 합니다.")
    private int anger;

    @Min(value = 1, message = "피로감 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "피로감 점수는 10점 이하여야 합니다.")
    private int fatigue;

    @Min(value = 1, message = "외로움 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "외로움 점수는 10점 이하여야 합니다.")
    private int loneliness;

    @Min(value = 1, message = "안정감 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "안정감 점수는 10점 이하여야 합니다.")
    private int stability;

    @Min(value = 1, message = "자기만족도 점수는 1점 이상이어야 합니다.")
    @Max(value = 10, message = "자기만족도 점수는 10점 이하여야 합니다.")
    private int selfSatisfaction;

    @Size(max = 4000, message = "코멘트는 4000자 이하로 입력해주세요.")
    private String comments;
}

