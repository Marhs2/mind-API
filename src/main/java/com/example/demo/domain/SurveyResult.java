package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_results")
@Getter
@Setter
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_results_seq")
    @SequenceGenerator(name = "survey_results_seq", sequenceName = "survey_results_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "survey_type")
    private String surveyType;

    private Integer depression;
    private Integer anxiety;
    private Integer joy;
    private Integer anger;
    private Integer fatigue;
    private Integer loneliness;
    private Integer stability;

    @Column(name = "self_satisfaction")
    private Integer selfSatisfaction;

    @Column(length = 4000)
    private String comments;

    @CreationTimestamp
    @Column(name = "submission_date", updatable = false)
    private LocalDateTime submissionDate;
}
