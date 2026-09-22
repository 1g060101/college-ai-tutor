package com.aistudy.tutor.domain.studyplan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预习复习计划实体（表 study_plan）
 */
@Getter
@Entity
@Table(name = "study_plan")
@NoArgsConstructor
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(nullable = false, length = 128)
    private String title;

    /** 周期：WEEK/MONTH/EXAM */
    @Column(nullable = false, length = 20)
    private String period = "WEEK";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** 状态：ACTIVE/DONE/CANCELED */
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudyPlan(Long userId, Long courseId, String title, String period, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
