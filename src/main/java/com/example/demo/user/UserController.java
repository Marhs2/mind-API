package com.example.demo.user;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.User;
import com.example.demo.service.StatisticsService;
import com.example.demo.service.UserService;
import com.example.demo.stats.GroupTrendResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final StatisticsService statisticsService;

    public UserController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        UserDto userDto = new UserDto(user);
        ApiResponse<UserDto> response = ApiResponse.success("사용자 정보를 성공적으로 조회했습니다.", userDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/statistics")
    public ResponseEntity<ApiResponse<GroupTrendResponseDto>> getMyStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        // 개인 누적·레이더·비교·평균 데이터
        GroupTrendResponseDto statistics = statisticsService.getPersonalStatistics(userDetails.getUsername());
        ApiResponse<GroupTrendResponseDto> response = ApiResponse.success("통계 데이터를 성공적으로 조회했습니다.", statistics);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/statistics/by-consultation")
    public ResponseEntity<ApiResponse<java.util.List<com.example.demo.stats.ConsultationStatDto>>> getStatisticsByConsultation(@AuthenticationPrincipal UserDetails userDetails) {
        java.util.List<com.example.demo.stats.ConsultationStatDto> stats = statisticsService.getStatisticsByConsultation(userDetails.getUsername());
        ApiResponse<java.util.List<com.example.demo.stats.ConsultationStatDto>> response = ApiResponse.success("상담별 감정 변화 통계를 성공적으로 조회했습니다.", stats);
        return ResponseEntity.ok(response);
    }
}
