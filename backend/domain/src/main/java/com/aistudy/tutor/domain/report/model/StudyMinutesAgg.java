package com.aistudy.tutor.domain.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习时长日聚合：支撑周 / 月学习报告。
 */
@Getter
@Entity
@Table(name = "study_minutes_agg")
@NoArgsConstructor
public class StudyMinutesAgg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "study_minutes", nullable = false)
    private int studyMinutes;

    @Column(name = "focus_minutes", nullable = false)
    private int focusMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
