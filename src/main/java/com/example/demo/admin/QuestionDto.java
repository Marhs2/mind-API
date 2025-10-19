package com.example.demo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDto {

    @NotBlank(message = "질문 내용을 입력해주세요.")
    @Size(max = 1000, message = "질문 내용은 1000자 이하로 입력해주세요.")
    private String text;

    @NotEmpty(message = "점수 범위를 입력해주세요.")
    @Size(min = 2, max = 2, message = "점수 범위는 최소값과 최대값 2개를 입력해주세요.")
    private List<Integer> scoreRange;
}