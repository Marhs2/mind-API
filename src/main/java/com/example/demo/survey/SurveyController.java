package com.example.demo.survey;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.SurveyResult;
import com.example.demo.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SurveyResponseDto>> submitSurvey(@Valid @RequestBody SurveyRequestDto request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        SurveyResult savedSurvey = surveyService.submitSurvey(request, userDetails);
        String surveyId = savedSurvey.getId().toString();
        SurveyResponseDto surveyResponse = new SurveyResponseDto("설문이 성공적으로 제출되었습니다.", surveyId);
        ApiResponse<SurveyResponseDto> response = ApiResponse.success("설문 제출이 완료되었습니다.", surveyResponse);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/my-surveys")
    public ResponseEntity<ApiResponse<List<SurveyResult>>> getMySurveys(@AuthenticationPrincipal UserDetails userDetails) {
        List<SurveyResult> surveys = surveyService.getMySurveys(userDetails);
        ApiResponse<List<SurveyResult>> response = ApiResponse.success("내 설문 목록을 성공적으로 조회했습니다.", surveys);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<LatestSurveyResponseDto>> getLatestSurvey(@AuthenticationPrincipal UserDetails userDetails) {
        LatestSurveyResponseDto latestSurvey = surveyService.getLatestSurvey(userDetails);
        if (latestSurvey == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("No surveys found for user", "NOT_FOUND"));
        }
        ApiResponse<LatestSurveyResponseDto> response = ApiResponse.success("최신 설문 결과를 성공적으로 조회했습니다.", latestSurvey);
        return ResponseEntity.ok(response);
    }
}

