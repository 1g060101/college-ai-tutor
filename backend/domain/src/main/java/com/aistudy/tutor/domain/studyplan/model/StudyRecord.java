package com.aistudy.tutor.domain.studyplan.model;

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
 * 学习记录实体（表 study_record，无软删字段）
 */
@Getter
@Entity
@Table(name = "study_record")
@NoArgsConstructor
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    /** 记录类型：STUDY/REVIEW/EXAM */
    @Column(name = "record_type", nullable = false, length = 20)
    private String recordType;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StudyRecord(Long userId, Long courseId, Long knowledgePointId, int durationSeconds,
                       String recordType, LocalDate recordDate) {
        this.userId = userId;
        this.courseId = courseId;
        this.knowledgePointId = knowledgePointId;
        this.durationSeconds = durationSeconds;
        this.recordType = recordType;
        this.recordDate = recordDate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
