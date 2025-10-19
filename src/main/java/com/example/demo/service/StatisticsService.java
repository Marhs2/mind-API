package com.example.demo.service;

import com.example.demo.domain.SurveyResult;
import com.example.demo.domain.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConsultationRepository;
import com.example.demo.repository.SurveyResultRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.stats.EmotionDistributionDto;
import com.example.demo.stats.EmotionDatasetDto;

import com.example.demo.stats.GroupTrendResponseDto;
import com.example.demo.stats.SelectiveAverageRequestDto;
import com.example.demo.stats.SelectiveAverageResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;
    private final ConsultationRepository consultationRepository;


    public StatisticsService(SurveyResultRepository surveyResultRepository, UserRepository userRepository, ConsultationRepository consultationRepository) {
        this.surveyResultRepository = surveyResultRepository;
        this.userRepository = userRepository;
        this.consultationRepository = consultationRepository;
    }

    /**
     * 전체 사용자의 감정 통계 요약 조회 (최근 7일)
     */
    public GroupTrendResponseDto getGroupStatisticsSummary(String emotions, String filterBy, String filterValue) {
        // 1. 날짜 범위 정의 (오늘 포함 7일)
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(6).toLocalDate().atStartOfDay();

        // 2. 필터 조건에 따라 데이터 조회
        List<SurveyResult> recentSurveys;
        if (filterBy != null && filterValue != null && !filterValue.isBlank()) {
            if ("role".equalsIgnoreCase(filterBy)) {
                List<User> filteredUsers = userRepository.findByRole(filterValue.toUpperCase());
                if (filteredUsers.isEmpty()) return createEmptyResponse();
                recentSurveys = surveyResultRepository.findByUserInAndSubmissionDateBetween(filteredUsers, start, end);
            } else if ("occupation".equalsIgnoreCase(filterBy)) {
                List<User> filteredUsers = userRepository.findByOccupation(filterValue);
                if (filteredUsers.isEmpty()) return createEmptyResponse();
                recentSurveys = surveyResultRepository.findByUserInAndSubmissionDateBetween(filteredUsers, start, end);
            } else if ("age".equalsIgnoreCase(filterBy)) {
                try {
                    String[] ageRange = filterValue.split("-");
                    int startAge = Integer.parseInt(ageRange[0]);
                    int endAge = Integer.parseInt(ageRange[1]);
                    List<User> filteredUsers = userRepository.findByAgeBetween(startAge, endAge);
                    if (filteredUsers.isEmpty()) return createEmptyResponse();
                    recentSurveys = surveyResultRepository.findByUserInAndSubmissionDateBetween(filteredUsers, start, end);
                } catch (Exception e) {
                    // Age range parsing error, return empty
                    return createEmptyResponse();
                }
            } else {
                recentSurveys = surveyResultRepository.findBySubmissionDateBetween(start, end);
            }
        } else {
            recentSurveys = surveyResultRepository.findBySubmissionDateBetween(start, end);
        }

        if (recentSurveys.isEmpty()) {
            return createEmptyResponse();
        }

        // 3. 날짜 라벨 생성
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        for (int i = 0; i < 7; i++) {
            labels.add(start.plusDays(i).format(formatter));
        }

        // 4. 일별 데이터셋 생성
        List<EmotionDatasetDto> datasets = createDailyEmotionDatasets(recentSurveys, labels, emotions);

        GroupTrendResponseDto response = new GroupTrendResponseDto();
        response.setLabels(labels);
        response.setDatasets(datasets);
        
        return response;
    }

    /**
     * 개인 사용자의 감정 통계 조회 (최근 4주)
     */
    public GroupTrendResponseDto getPersonalStatistics(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 1. 날짜 범위 정의 (오늘이 속한 주 포함, 4주)
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusWeeks(3).with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();

        // 2. 해당 기간의 사용자 데이터만 조회
        List<SurveyResult> recentSurveys = surveyResultRepository.findByUserAndSubmissionDateBetween(user, start, end);

        if (recentSurveys.isEmpty()) {
            return createEmptyResponse();
        }

        // 3. 내부 처리를 위한 주간 라벨 생성 (각 주의 월요일)
        List<String> internalLabels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        for (int i = 3; i >= 0; i--) {
            internalLabels.add(end.minusWeeks(i).with(DayOfWeek.MONDAY).format(formatter));
        }
        
        // 4. 주간 데이터셋 생성
        List<EmotionDatasetDto> datasets = createWeeklyEmotionDatasets(recentSurveys, internalLabels);

        // 5. 사용자에게 보여줄 최종 라벨 생성 (e.g., "09/29 - 10/05")
        List<String> displayLabels = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime currentWeek = end.minusWeeks(i);
            LocalDateTime weekStart = currentWeek.with(DayOfWeek.MONDAY);
            LocalDateTime weekEnd = weekStart.plusDays(6); // 월요일 + 6일 = 일요일
            displayLabels.add(weekStart.format(formatter) + " - " + weekEnd.format(formatter));
        }

        GroupTrendResponseDto response = new GroupTrendResponseDto();
        response.setLabels(displayLabels);
        response.setDatasets(datasets);
        
        return response;
    }

    private GroupTrendResponseDto createEmptyResponse() {
        GroupTrendResponseDto response = new GroupTrendResponseDto();
        response.setLabels(new ArrayList<>());
        response.setDatasets(new ArrayList<>());
        return response;
    }

    private List<EmotionDatasetDto> createDailyEmotionDatasets(List<SurveyResult> surveys, List<String> labels, String emotionsFilter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        Map<String, List<SurveyResult>> surveysByDate = surveys.stream()
                .filter(s -> s.getSubmissionDate() != null)
                .collect(Collectors.groupingBy(s -> s.getSubmissionDate().format(formatter)));

        return buildEmotionDatasets(surveysByDate, labels, emotionsFilter);
    }

    private List<EmotionDatasetDto> createWeeklyEmotionDatasets(List<SurveyResult> surveys, List<String> labels) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        Map<String, List<SurveyResult>> surveysByWeek = surveys.stream()
                .filter(s -> s.getSubmissionDate() != null)
                .collect(Collectors.groupingBy(s -> s.getSubmissionDate().with(DayOfWeek.MONDAY).format(formatter)));
        
        return buildEmotionDatasets(surveysByWeek, labels, null);
    }

    private List<EmotionDatasetDto> buildEmotionDatasets(Map<String, List<SurveyResult>> groupedSurveys, List<String> labels, String emotionsFilter) {
        Map<String, String> emotionMap = getEmotionMap();
        Set<String> selectedEmotions = parseEmotionsFilter(emotionsFilter);

        List<EmotionDatasetDto> datasets = new ArrayList<>();
        for (Map.Entry<String, String> entry : emotionMap.entrySet()) {
            String emotionKey = entry.getKey();
            if (!selectedEmotions.isEmpty() && !selectedEmotions.contains(emotionKey)) {
                continue;
            }

            EmotionDatasetDto dataset = new EmotionDatasetDto();
            dataset.setLabel(entry.getValue());

            List<Integer> averages = labels.stream()
                    .map(label -> {
                        List<SurveyResult> periodSurveys = groupedSurveys.get(label);
                        if (periodSurveys == null || periodSurveys.isEmpty()) {
                            return 0;
                        }
                        double average = periodSurveys.stream()
                                .mapToInt(survey -> getEmotionValue(survey, emotionKey))
                                .average()
                                .orElse(0.0);
                        return (int) Math.round(average);
                    })
                    .collect(Collectors.toList());

            dataset.setData(averages);
            datasets.add(dataset);
        }
        return datasets;
    }

    private Integer getEmotionValue(SurveyResult survey, String emotionKey) {
        switch (emotionKey) {
            case "depression": return survey.getDepression() != null ? survey.getDepression() : 0;
            case "anxiety": return survey.getAnxiety() != null ? survey.getAnxiety() : 0;
            case "joy": return survey.getJoy() != null ? survey.getJoy() : 0;
            case "anger": return survey.getAnger() != null ? survey.getAnger() : 0;
            case "fatigue": return survey.getFatigue() != null ? survey.getFatigue() : 0;
            case "loneliness": return survey.getLoneliness() != null ? survey.getLoneliness() : 0;
            case "stability": return survey.getStability() != null ? survey.getStability() : 0;
            case "selfSatisfaction": return survey.getSelfSatisfaction() != null ? survey.getSelfSatisfaction() : 0;
            default: return 0;
        }
    }

    private Map<String, String> getEmotionMap() {
        Map<String, String> emotionMap = new LinkedHashMap<>();
        emotionMap.put("depression", "우울감");
        emotionMap.put("anxiety", "불안감");
        emotionMap.put("joy", "기쁨");
        emotionMap.put("anger", "분노");
        emotionMap.put("fatigue", "피로감");
        emotionMap.put("loneliness", "외로움");
        emotionMap.put("stability", "안정감");
        emotionMap.put("selfSatisfaction", "자기만족도");
        return emotionMap;
    }

    private Set<String> parseEmotionsFilter(String emotionsFilter) {
        if (emotionsFilter == null || emotionsFilter.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(emotionsFilter.split(",")));
    }

    public com.example.demo.stats.CorrelationMatrixDto getEmotionCorrelation() {
        List<SurveyResult> allSurveys = surveyResultRepository.findAll();

        // 상관관계를 계산하기에 데이터가 충분하지 않으면 빈 응답 반환
        if (allSurveys.size() < 2) {
            com.example.demo.stats.CorrelationMatrixDto emptyDto = new com.example.demo.stats.CorrelationMatrixDto();
            emptyDto.setLabels(new ArrayList<>());
            emptyDto.setMatrix(new double[0][0]);
            return emptyDto;
        }

        Map<String, String> emotionMap = getEmotionMap();
        List<String> emotionKeys = new ArrayList<>(emotionMap.keySet());
        int numEmotions = emotionKeys.size();
        int numSurveys = allSurveys.size();

        // 각 감정별 점수 배열 생성
        Map<String, double[]> emotionData = new HashMap<>();
        for (String key : emotionKeys) {
            emotionData.put(key, new double[numSurveys]);
        }

        for (int i = 0; i < numSurveys; i++) {
            SurveyResult survey = allSurveys.get(i);
            for (String key : emotionKeys) {
                emotionData.get(key)[i] = getEmotionValue(survey, key);
            }
        }

        // 상관관계 행렬 계산
        double[][] correlationMatrix = new double[numEmotions][numEmotions];
        org.apache.commons.math3.stat.correlation.PearsonsCorrelation correlation = new org.apache.commons.math3.stat.correlation.PearsonsCorrelation();

        for (int i = 0; i < numEmotions; i++) {
            for (int j = 0; j < numEmotions; j++) {
                double[] x = emotionData.get(emotionKeys.get(i));
                double[] y = emotionData.get(emotionKeys.get(j));
                correlationMatrix[i][j] = correlation.correlation(x, y);
            }
        }

        com.example.demo.stats.CorrelationMatrixDto dto = new com.example.demo.stats.CorrelationMatrixDto();
        dto.setLabels(new ArrayList<>(emotionMap.values()));
        dto.setMatrix(correlationMatrix);

        return dto;
    }

    public List<com.example.demo.stats.ConsultationStatDto> getStatisticsByConsultation(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        List<com.example.demo.domain.Consultation> consultations = consultationRepository.findByUserIdOrderByConsultationDateDesc(user.getId());

        return consultations.stream()
                .map(this::createConsultationStatDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<com.example.demo.stats.ConsultationStatDto> createConsultationStatDto(com.example.demo.domain.Consultation consultation) {
        Optional<SurveyResult> beforeSurvey = consultation.getSurveyResults().stream()
                .filter(r -> "Before".equalsIgnoreCase(r.getSurveyType())).findFirst();

        Optional<SurveyResult> afterSurvey = consultation.getSurveyResults().stream()
                .filter(r -> "After".equalsIgnoreCase(r.getSurveyType())).findFirst();

        if (beforeSurvey.isEmpty() || afterSurvey.isEmpty()) {
            return Optional.empty();
        }

        SurveyResult before = beforeSurvey.get();
        SurveyResult after = afterSurvey.get();

        com.example.demo.stats.ConsultationStatDto statDto = new com.example.demo.stats.ConsultationStatDto();
        statDto.setConsultationId(consultation.getId());
        statDto.setConsultationDate(consultation.getConsultationDate());

        List<com.example.demo.stats.EmotionChangeDto> changes = new ArrayList<>();
        Map<String, String> emotionMap = getEmotionMap();

        for (String emotionKey : emotionMap.keySet()) {
            int beforeScore = getEmotionValue(before, emotionKey);
            int afterScore = getEmotionValue(after, emotionKey);

            com.example.demo.stats.EmotionChangeDto changeDto = new com.example.demo.stats.EmotionChangeDto();
            changeDto.setEmotionName(emotionMap.get(emotionKey));
            changeDto.setBeforeScore(beforeScore);
            changeDto.setAfterScore(afterScore);
            changeDto.setChange(afterScore - beforeScore);
            changes.add(changeDto);
        }

        statDto.setEmotionChanges(changes);
        return Optional.of(statDto);
    }

    @Transactional(readOnly = true)
    public SelectiveAverageResponseDto getSelectiveAverage(SelectiveAverageRequestDto requestDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(requestDto.getPeriod().getStart(), formatter);
        LocalDate endDate = LocalDate.parse(requestDto.getPeriod().getEnd(), formatter);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<String> emotionKeys = requestDto.getEmotionKeys();

        List<SurveyResult> results = surveyResultRepository.findBySubmissionDateBetween(startDateTime, endDateTime);

        List<SurveyResult> beforeSurveys = results.stream()
                .filter(r -> "Before".equalsIgnoreCase(r.getSurveyType()))
                .collect(Collectors.toList());

        List<SurveyResult> afterSurveys = results.stream()
                .filter(r -> "After".equalsIgnoreCase(r.getSurveyType()))
                .collect(Collectors.toList());

        double averageBefore = calculateAverage(beforeSurveys, emotionKeys);
        double averageAfter = calculateAverage(afterSurveys, emotionKeys);

        double improvement = 0;
        if (averageBefore > 0) {
            improvement = ((averageBefore - averageAfter) / averageBefore) * 100;
        }

        SelectiveAverageResponseDto responseDto = new SelectiveAverageResponseDto();
        responseDto.setAverageBefore(averageBefore);
        responseDto.setAverageAfter(averageAfter);
        responseDto.setImprovement(String.format("%.1f%%", improvement));

        return responseDto;
    }

    private double calculateAverage(List<SurveyResult> surveys, List<String> emotionKeys) {
        if (surveys.isEmpty() || emotionKeys.isEmpty()) {
            return 0.0;
        }

        double totalSum = 0;
        int totalCount = 0;

        for (SurveyResult survey : surveys) {
            for (String key : emotionKeys) {
                totalSum += getEmotionValue(survey, key);
                totalCount++;
            }
        }

        return totalCount > 0 ? totalSum / totalCount : 0.0;
    }

    public List<EmotionDistributionDto> getEmotionDistribution() {
        List<SurveyResult> allSurveys = surveyResultRepository.findAll();
        Map<String, String> emotionMap = getEmotionMap();
        List<EmotionDistributionDto> distributionList = new ArrayList<>();

        List<String> scoreLabels = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            scoreLabels.add(String.valueOf(i));
        }

        for (Map.Entry<String, String> entry : emotionMap.entrySet()) {
            String emotionKey = entry.getKey();
            String emotionName = entry.getValue();

            Map<Integer, Integer> scoreCounts = new HashMap<>();
            for (int i = 0; i <= 10; i++) {
                scoreCounts.put(i, 0);
            }

            for (SurveyResult survey : allSurveys) {
                int score = getEmotionValue(survey, emotionKey);
                scoreCounts.put(score, scoreCounts.get(score) + 1);
            }

            List<Integer> data = new ArrayList<>();
            for (int i = 0; i <= 10; i++) {
                data.add(scoreCounts.get(i));
            }

            distributionList.add(new EmotionDistributionDto(emotionName, scoreLabels, data));
        }

        return distributionList;
    }

    @Transactional(readOnly = true)
    public com.example.demo.stats.PersonalAveragesResponseDto getMyEmotionAverages(org.springframework.security.core.userdetails.UserDetails currentUserDetails) {
        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + currentUserDetails.getUsername()));

        List<SurveyResult> surveys = surveyResultRepository.findByUserOrderBySubmissionDateDesc(user);

        if (surveys.isEmpty()) {
            return new com.example.demo.stats.PersonalAveragesResponseDto(java.util.Collections.emptyList());
        }

        Map<String, String> emotionMap = getEmotionMap();
        List<com.example.demo.stats.EmotionAverage> averages = new java.util.ArrayList<>();

        for (Map.Entry<String, String> entry : emotionMap.entrySet()) {
            String emotionKey = entry.getKey();
            String emotionName = entry.getValue();

            double average = surveys.stream()
                    .mapToInt(survey -> getEmotionValue(survey, emotionKey))
                    .average()
                    .orElse(0.0);

            // Format to one decimal place
            double formattedAverage = Math.round(average * 10.0) / 10.0;

            averages.add(new com.example.demo.stats.EmotionAverage(emotionName, formattedAverage));
        }

        return new com.example.demo.stats.PersonalAveragesResponseDto(averages);
    }

    @Transactional(readOnly = true)
    public com.example.demo.stats.UserGroupComparisonDto getUserGroupComparison(Long userId) {
        // Get user-specific averages
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        List<SurveyResult> userSurveys = surveyResultRepository.findByUserOrderBySubmissionDateDesc(user);
        List<com.example.demo.stats.EmotionAverage> userAverages = calculateAveragesForSurveys(userSurveys);

        // Get group averages
        List<SurveyResult> allSurveys = surveyResultRepository.findAll();
        List<com.example.demo.stats.EmotionAverage> groupAverages = calculateAveragesForSurveys(allSurveys);

        return new com.example.demo.stats.UserGroupComparisonDto(userAverages, groupAverages);
    }

    private List<com.example.demo.stats.EmotionAverage> calculateAveragesForSurveys(List<SurveyResult> surveys) {
        if (surveys.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        Map<String, String> emotionMap = getEmotionMap();
        List<com.example.demo.stats.EmotionAverage> averages = new java.util.ArrayList<>();

        for (Map.Entry<String, String> entry : emotionMap.entrySet()) {
            String emotionKey = entry.getKey();
            String emotionName = entry.getValue();

            double average = surveys.stream()
                    .mapToInt(survey -> getEmotionValue(survey, emotionKey))
                    .average()
                    .orElse(0.0);

            double formattedAverage = Math.round(average * 10.0) / 10.0;
            averages.add(new com.example.demo.stats.EmotionAverage(emotionName, formattedAverage));
        }
        return averages;
    }
}
