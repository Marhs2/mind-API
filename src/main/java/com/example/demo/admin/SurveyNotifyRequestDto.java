package com.example.demo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyNotifyRequestDto {
    
    @NotBlank(message = "설문 타입을 입력해주세요.")
    @Pattern(regexp = "^(Before|After)$", message = "설문 타입은 'Before' 또는 'After'만 입력 가능합니다.")
    private String surveyType; // Before | After
}

