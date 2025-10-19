package com.example.demo.survey;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/consultations")
public class UserConsultationController {

    private final SurveyService surveyService;

    public UserConsultationController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<ConsultationSummaryDto>>> getMyCounselingHistory(@AuthenticationPrincipal UserDetails userDetails) {
        List<ConsultationSummaryDto> history = surveyService.getConsultationHistory(userDetails);
        ApiResponse<List<ConsultationSummaryDto>> response = ApiResponse.success("상담 이력을 성공적으로 조회했습니다.", history);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.example.demo.survey.ConsultationDetailDto>> getConsultationDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        com.example.demo.survey.ConsultationDetailDto detail = surveyService.getConsultationById(id, userDetails);
        ApiResponse<com.example.demo.survey.ConsultationDetailDto> response = ApiResponse.success("상담 상세 정보를 성공적으로 조회했습니다.", detail);
        return ResponseEntity.ok(response);
    }
}

