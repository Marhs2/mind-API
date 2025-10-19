package com.example.demo.service;

import com.example.demo.admin.AdminSurveyResponseDto;
import com.example.demo.admin.CustomSurveyDto;
import com.example.demo.domain.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.survey.ConsultationDetailDto;
import com.example.demo.survey.ConsultationSummaryDto;
import com.example.demo.survey.SurveyRequestDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;
    private final ConsultationRepository consultationRepository;
    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyResultRepository surveyResultRepository, UserRepository userRepository, ConsultationRepository consultationRepository, SurveyRepository surveyRepository) {
        this.surveyResultRepository = surveyResultRepository;
        this.userRepository = userRepository;
        this.consultationRepository = consultationRepository;
        this.surveyRepository = surveyRepository;
    }

    @Transactional
    public SurveyResult submitSurvey(SurveyRequestDto requestDto, UserDetails currentUserDetails) {
        if (requestDto == null) {
            throw new IllegalArgumentException("설문 데이터가 입력되지 않았습니다.");
        }
        
        if (currentUserDetails == null || currentUserDetails.getUsername() == null) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }

        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + currentUserDetails.getUsername()));

        Consultation consultation = null;

        // "Before" survey: Create a new consultation
        if ("Before".equalsIgnoreCase(requestDto.getSurveyType())) {
            consultation = new Consultation();
            consultation.setUser(user);
            consultation.setConsultationDate(LocalDateTime.now());
            consultation.setStatus("IN_PROGRESS");
            consultation = consultationRepository.save(consultation);
        } else if (requestDto.getConsultationId() != null && !requestDto.getConsultationId().isBlank()) {
            // "After" survey or others with a specific consultation ID
            try {
                long consultationId = Long.parseLong(requestDto.getConsultationId());
                consultation = consultationRepository.findById(consultationId)
                        .orElse(null);
                
                if (consultation == null) {
                    System.out.println("경고: 상담 ID " + consultationId + "에 해당하는 상담 정보가 없습니다. 일반 설문으로 처리됩니다.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("상담 ID 형식이 올바르지 않습니다: " + requestDto.getConsultationId());
            }
        }

        try {
            SurveyResult surveyResult = new SurveyResult();
            surveyResult.setUser(user);
            surveyResult.setConsultation(consultation);
            surveyResult.setSurveyType(requestDto.getSurveyType());
            surveyResult.setDepression(requestDto.getDepression());
            surveyResult.setAnxiety(requestDto.getAnxiety());
            surveyResult.setJoy(requestDto.getJoy());
            surveyResult.setAnger(requestDto.getAnger());
            surveyResult.setFatigue(requestDto.getFatigue());
            surveyResult.setLoneliness(requestDto.getLoneliness());
            surveyResult.setStability(requestDto.getStability());
            surveyResult.setSelfSatisfaction(requestDto.getSelfSatisfaction());
            surveyResult.setComments(requestDto.getComments());

            return surveyResultRepository.save(surveyResult);
        } catch (Exception e) {
            throw new RuntimeException("설문 제출 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ConsultationSummaryDto> getConsultationHistory(UserDetails currentUserDetails) {
        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + currentUserDetails.getUsername()));

        List<Consultation> consultations = consultationRepository.findByUserIdOrderByConsultationDateDesc(user.getId());

        return consultations.stream()
                .map(this::mapToConsultationSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SurveyResult> getMySurveys(UserDetails currentUserDetails) {
        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + currentUserDetails.getUsername()));

        return surveyResultRepository.findByUserOrderBySubmissionDateDesc(user);
    }

    @Transactional(readOnly = true)
    public List<AdminSurveyResponseDto> getSurveysByUserId(Long userId) {
        return surveyResultRepository.findByUserIdOrderBySubmissionDateDesc(userId).stream()
                .map(AdminSurveyResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.example.demo.admin.CustomSurveyResponseDto> getAllCustomSurveys() {
        return surveyRepository.findAll().stream()
                .map(com.example.demo.admin.CustomSurveyResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Survey updateCustomSurvey(Long surveyId, CustomSurveyDto customSurveyDto) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));

        survey.setTitle(customSurveyDto.getTitle());

        // Clear old questions and add new ones
        survey.getQuestions().clear();
        List<Question> newQuestions = customSurveyDto.getQuestions().stream()
                .map(questionDto -> {
                    Question question = new Question();
                    question.setText(questionDto.getText());
                    question.setMinScore(questionDto.getScoreRange().get(0));
                    question.setMaxScore(questionDto.getScoreRange().get(1));
                    question.setSurvey(survey);
                    return question;
                })
                .collect(Collectors.toList());
        survey.getQuestions().addAll(newQuestions);

        // Handle assigned users
        survey.getAssignedUsers().clear();
        if (customSurveyDto.getAssignedUserIds() != null && !customSurveyDto.getAssignedUserIds().isEmpty()) {
            List<User> assignedUsers = userRepository.findAllById(customSurveyDto.getAssignedUserIds());
            survey.getAssignedUsers().addAll(assignedUsers);
        }

        return surveyRepository.save(survey);
    }

    @Transactional
    public Survey createCustomSurvey(CustomSurveyDto customSurveyDto, UserDetails adminUserDetails) {
        User admin = userRepository.findByEmail(adminUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminUserDetails.getUsername()));

        Survey survey = new Survey();
        survey.setTitle(customSurveyDto.getTitle());
        survey.setCreatedBy(admin);

        List<Question> questions = customSurveyDto.getQuestions().stream()
                .map(questionDto -> {
                    Question question = new Question();
                    question.setText(questionDto.getText());
                    question.setMinScore(questionDto.getScoreRange().get(0));
                    question.setMaxScore(questionDto.getScoreRange().get(1));
                    question.setSurvey(survey); // Set the bidirectional relationship
                    return question;
                })
                .collect(Collectors.toList());

        survey.getQuestions().addAll(questions);

        // Handle assigned users
        if (customSurveyDto.getAssignedUserIds() != null && !customSurveyDto.getAssignedUserIds().isEmpty()) {
            List<User> assignedUsers = userRepository.findAllById(customSurveyDto.getAssignedUserIds());
            survey.getAssignedUsers().addAll(assignedUsers);
        }

        return surveyRepository.save(survey);
    }

    @Transactional
    public void deleteCustomSurvey(Long surveyId) {
        surveyRepository.deleteById(surveyId);
    }

    public ConsultationDetailDto getConsultationById(Long id, UserDetails currentUserDetails) {
        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserDetails.getUsername()));

        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found: " + id));

        // Check if the consultation belongs to the current user
        if (!consultation.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view this consultation.");
        }

        ConsultationDetailDto detailDto = new ConsultationDetailDto();
        detailDto.setConsultationId(consultation.getId().toString());

        consultation.getSurveyResults().forEach(surveyResult -> {
            SurveyRequestDto surveyDto = new SurveyRequestDto();
            surveyDto.setSurveyType(surveyResult.getSurveyType());
            surveyDto.setDepression(surveyResult.getDepression());
            surveyDto.setAnxiety(surveyResult.getAnxiety());
            surveyDto.setJoy(surveyResult.getJoy());
            surveyDto.setAnger(surveyResult.getAnger());
            surveyDto.setFatigue(surveyResult.getFatigue());
            surveyDto.setLoneliness(surveyResult.getLoneliness());
            surveyDto.setStability(surveyResult.getStability());
            surveyDto.setSelfSatisfaction(surveyResult.getSelfSatisfaction());
            surveyDto.setComments(surveyResult.getComments());
            if (surveyResult.getConsultation() != null) {
                surveyDto.setConsultationId(surveyResult.getConsultation().getId().toString());
            }

            if ("before".equalsIgnoreCase(surveyResult.getSurveyType())) {
                detailDto.setBefore(surveyDto);
            } else if ("after".equalsIgnoreCase(surveyResult.getSurveyType())) {
                detailDto.setAfter(surveyDto);
            }
        });

        return detailDto;
    }

    private ConsultationSummaryDto mapToConsultationSummaryDto(Consultation consultation) {
        ConsultationSummaryDto dto = new ConsultationSummaryDto();
        dto.setConsultationId(consultation.getId().toString());
        dto.setStatus(consultation.getStatus());
        if (consultation.getConsultationDate() != null) {
            dto.setCreatedDate(consultation.getConsultationDate().format(DateTimeFormatter.ISO_DATE));
        }

        boolean hasBefore = consultation.getSurveyResults().stream()
                .anyMatch(r -> "before".equalsIgnoreCase(r.getSurveyType()));
        boolean hasAfter = consultation.getSurveyResults().stream()
                .anyMatch(r -> "after".equalsIgnoreCase(r.getSurveyType()));

        dto.setHasBeforeSurvey(hasBefore);
        dto.setHasAfterSurvey(hasAfter);

        return dto;
    }

    @Transactional(readOnly = true)
    public com.example.demo.survey.LatestSurveyResponseDto getLatestSurvey(UserDetails currentUserDetails) {
        User user = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + currentUserDetails.getUsername()));

        List<SurveyResult> surveys = surveyResultRepository.findByUserOrderBySubmissionDateDesc(user);

        if (surveys.isEmpty()) {
            return null; // Or throw a specific exception
        }

        SurveyResult latestSurvey = surveys.get(0);
        List<com.example.demo.survey.EmotionScore> scores = java.util.Arrays.asList(
                new com.example.demo.survey.EmotionScore("우울함", latestSurvey.getDepression()),
                new com.example.demo.survey.EmotionScore("불안", latestSurvey.getAnxiety()),
                new com.example.demo.survey.EmotionScore("기쁨", latestSurvey.getJoy()),
                new com.example.demo.survey.EmotionScore("분노", latestSurvey.getAnger()),
                new com.example.demo.survey.EmotionScore("피로", latestSurvey.getFatigue()),
                new com.example.demo.survey.EmotionScore("외로움", latestSurvey.getLoneliness()),
                new com.example.demo.survey.EmotionScore("안정감", latestSurvey.getStability()),
                new com.example.demo.survey.EmotionScore("자기만족도", latestSurvey.getSelfSatisfaction())
        );

        return new com.example.demo.survey.LatestSurveyResponseDto(scores, latestSurvey.getComments());
    }
}