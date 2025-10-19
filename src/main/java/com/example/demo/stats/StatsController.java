package com.example.demo.stats;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.StatisticsService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatisticsService statisticsService;
    private final UserService userService;

    public StatsController(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    @GetMapping("/me/trends")
    public ResponseEntity<ApiResponse<GroupTrendResponseDto>> getMyTrends(@AuthenticationPrincipal UserDetails userDetails) {
        GroupTrendResponseDto statistics = statisticsService.getPersonalStatistics(userDetails.getUsername());
        ApiResponse<GroupTrendResponseDto> response = ApiResponse.success("나의 감정 변화 추이를 성공적으로 조회했습니다.", statistics);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/averages")
    public ResponseEntity<ApiResponse<PersonalAveragesResponseDto>> getMyAverages(@AuthenticationPrincipal UserDetails userDetails) {
        PersonalAveragesResponseDto averages = statisticsService.getMyEmotionAverages(userDetails);
        ApiResponse<PersonalAveragesResponseDto> response = ApiResponse.success("나의 감정별 평균을 성공적으로 조회했습니다.", averages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/comparison")
    public ResponseEntity<ApiResponse<java.util.List<ConsultationStatDto>>> getMyComparisonStats(@AuthenticationPrincipal UserDetails userDetails) {
        java.util.List<ConsultationStatDto> comparisonStats = statisticsService.getStatisticsByConsultation(userDetails.getUsername());
        ApiResponse<java.util.List<ConsultationStatDto>> response = ApiResponse.success("나의 상담별 감정 변화를 성공적으로 조회했습니다.", comparisonStats);
        return ResponseEntity.ok(response);
    }
}
