package com.example.demo.repository;

import com.example.demo.domain.SurveyResult;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findByUserOrderBySubmissionDateDesc(User user);
    List<SurveyResult> findByUserIdOrderBySubmissionDateDesc(Long userId);
    List<SurveyResult> findBySubmissionDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<SurveyResult> findByUserAndSubmissionDateBetween(User user, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<SurveyResult> findByUserInAndSubmissionDateBetween(java.util.List<User> users, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
