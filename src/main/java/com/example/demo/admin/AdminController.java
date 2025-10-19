package com.example.demo.admin;

import com.example.demo.auth.UserRegisterRequestDto;
import com.example.demo.common.ApiResponse;
import com.example.demo.domain.SurveyResult;
import com.example.demo.domain.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.StatisticsService;
import com.example.demo.service.SurveyService;
import com.example.demo.service.UserService;
import com.example.demo.stats.EmotionDistributionDto;
import com.example.demo.stats.GroupTrendResponseDto;

import com.example.demo.user.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final StatisticsService statisticsService;
    private final EmailService emailService;
    private final UserService userService;
    private final SurveyService surveyService;

    public AdminController(StatisticsService statisticsService, EmailService emailService, UserService userService, SurveyService surveyService) {
        this.statisticsService = statisticsService;
        this.emailService = emailService;
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers(@RequestParam(required = false) String search) {
        // 사용자 목록 조회
        List<User> userList = userService.getUsers(search);
        List<UserDto> users = userList.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
        
        ApiResponse<List<UserDto>> response = ApiResponse.success("사용자 목록을 성공적으로 조회했습니다.", users);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
        User newUser = userService.createUserByAdmin(requestDto);
        UserDto userDto = new UserDto(newUser);
        ApiResponse<UserDto> response = ApiResponse.success("관리자가 사용자를 성공적으로 생성했습니다.", userDto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable int userId) {
        User user = userService.findById((long) userId);
        UserDto userDto = new UserDto(user);
        ApiResponse<UserDto> response = ApiResponse.success("사용자 정보를 성공적으로 조회했습니다.", userDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<MessageResponseDto>> updateUserStatus(@PathVariable int userId, @Valid @RequestBody UserDto userDto) {
        // 사용자 상태 수정
        userService.updateUser((long) userId, userDto);
        MessageResponseDto messageResponse = new MessageResponseDto("사용자 정보가 업데이트되었습니다.");
        ApiResponse<MessageResponseDto> response = ApiResponse.success("사용자 정보가 성공적으로 수정되었습니다.", messageResponse);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<MessageResponseDto>> updateUserEnabledStatus(@PathVariable int userId, @Valid @RequestBody UserStatusUpdateDto statusDto) {
        userService.updateUserEnabledStatus((long) userId, statusDto.getEnabled());
        String status = statusDto.getEnabled() ? "활성화" : "비활성화";
        MessageResponseDto messageResponse = new MessageResponseDto("사용자 계정이 성공적으로 " + status + "되었습니다.");
        ApiResponse<MessageResponseDto> response = ApiResponse.success("사용자 상태가 성공적으로 변경되었습니다.", messageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/surveys")
    public ResponseEntity<ApiResponse<List<AdminSurveyResponseDto>>> getUserSurveys(@PathVariable int userId) {
        // 특정 사용자 설문 조회
        List<AdminSurveyResponseDto> surveys = surveyService.getSurveysByUserId((long) userId);
        ApiResponse<List<AdminSurveyResponseDto>> response = ApiResponse.success("사용자 설문 목록을 성공적으로 조회했습니다.", surveys);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/send-survey-email")
    public ResponseEntity<ApiResponse<MessageResponseDto>> sendSurveyEmail(@PathVariable int userId, @Valid @RequestBody SurveyNotifyRequestDto request) {
        try {
            // 실제 이메일 발송
            emailService.sendSurveyRequestEmail(String.valueOf(userId), request.getSurveyType());
            
            MessageResponseDto messageResponse = new MessageResponseDto("설문 요청 이메일이 성공적으로 발송되었습니다.");
            ApiResponse<MessageResponseDto> response = ApiResponse.success("이메일이 성공적으로 발송되었습니다.", messageResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessageResponseDto messageResponse = new MessageResponseDto("이메일 발송에 실패했습니다: " + e.getMessage());
            ApiResponse<MessageResponseDto> response = ApiResponse.error("이메일 발송 실패", "EMAIL_SEND_FAILED");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/statistics/summary")
    public ResponseEntity<ApiResponse<GroupTrendResponseDto>> getStatisticsSummary(@RequestParam(required = false) String emotions, @RequestParam(required = false) String filterBy, @RequestParam(required = false) String filterValue) {
        // 집단 통계 요약
        GroupTrendResponseDto statistics = statisticsService.getGroupStatisticsSummary(emotions, filterBy, filterValue);
        ApiResponse<GroupTrendResponseDto> response = ApiResponse.success("통계 요약을 성공적으로 조회했습니다.", statistics);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/statistics/selective-average")
    public ResponseEntity<ApiResponse<com.example.demo.stats.SelectiveAverageResponseDto>> getSelectiveAverage(@Valid @RequestBody com.example.demo.stats.SelectiveAverageRequestDto requestDto) {
        com.example.demo.stats.SelectiveAverageResponseDto result = statisticsService.getSelectiveAverage(requestDto);
        ApiResponse<com.example.demo.stats.SelectiveAverageResponseDto> response = ApiResponse.success("선택적 통계 계산을 성공적으로 완료했습니다.", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/custom-surveys")
    public ResponseEntity<ApiResponse<List<CustomSurveyResponseDto>>> getCustomSurveys() {
        List<CustomSurveyResponseDto> surveys = surveyService.getAllCustomSurveys();
        ApiResponse<List<CustomSurveyResponseDto>> response = ApiResponse.success("커스텀 설문 목록을 성공적으로 조회했습니다.", surveys);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom-surveys")
    public ResponseEntity<ApiResponse<MessageResponseDto>> createCustomSurvey(@Valid @RequestBody CustomSurveyDto customSurveyDto, @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails adminUserDetails) {
        // 커스텀 설문 생성
        surveyService.createCustomSurvey(customSurveyDto, adminUserDetails);
        MessageResponseDto messageResponse = new MessageResponseDto("설문이 성공적으로 생성되었습니다.");
        ApiResponse<MessageResponseDto> response = ApiResponse.success("커스텀 설문이 성공적으로 생성되었습니다.", messageResponse);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/custom-surveys/{surveyId}")
    public ResponseEntity<ApiResponse<MessageResponseDto>> updateCustomSurvey(@PathVariable int surveyId, @Valid @RequestBody CustomSurveyDto customSurveyDto) {
        // 커스텀 설문 수정
        surveyService.updateCustomSurvey((long) surveyId, customSurveyDto);
        MessageResponseDto messageResponse = new MessageResponseDto("설문이 성공적으로 수정되었습니다.");
        ApiResponse<MessageResponseDto> response = ApiResponse.success("커스텀 설문이 성공적으로 수정되었습니다.", messageResponse);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/custom-surveys/{surveyId}")
    public ResponseEntity<ApiResponse<MessageResponseDto>> deleteCustomSurvey(@PathVariable int surveyId) {
        // 커스텀 설문 삭제
        surveyService.deleteCustomSurvey((long) surveyId);
        MessageResponseDto messageResponse = new MessageResponseDto("설문이 성공적으로 삭제되었습니다.");
        ApiResponse<MessageResponseDto> response = ApiResponse.success("커스텀 설문이 성공적으로 삭제되었습니다.", messageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/correlation")
    public ResponseEntity<ApiResponse<com.example.demo.stats.CorrelationMatrixDto>> getEmotionCorrelation() {
        com.example.demo.stats.CorrelationMatrixDto correlationMatrix = statisticsService.getEmotionCorrelation();
        ApiResponse<com.example.demo.stats.CorrelationMatrixDto> response = ApiResponse.success("감정 상관관계 매트릭스를 성공적으로 조회했습니다.", correlationMatrix);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/distribution")
    public ResponseEntity<ApiResponse<List<EmotionDistributionDto>>> getEmotionDistribution() {
        List<EmotionDistributionDto> distribution = statisticsService.getEmotionDistribution();
        ApiResponse<List<EmotionDistributionDto>> response = ApiResponse.success("감정 점수 분포를 성공적으로 조회했습니다.", distribution);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/user-group-comparison/{userId}")
    public ResponseEntity<ApiResponse<com.example.demo.stats.UserGroupComparisonDto>> getUserGroupComparison(@PathVariable Long userId) {
        com.example.demo.stats.UserGroupComparisonDto comparison = statisticsService.getUserGroupComparison(userId);
        ApiResponse<com.example.demo.stats.UserGroupComparisonDto> response = ApiResponse.success("사용자-그룹 평균 비교 데이터를 성공적으로 조회했습니다.", comparison);
        return ResponseEntity.ok(response);
    }
}
