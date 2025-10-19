package com.example.demo.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomSurveyDto {

    @NotBlank(message = "설문 제목을 입력해주세요.")
    @Size(max = 255, message = "설문 제목은 255자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "질문을 최소 1개 이상 입력해주세요.")
    @Valid // This enables validation for the objects in the list
    private List<QuestionDto> questions;

    private List<Long> assignedUserIds;
}