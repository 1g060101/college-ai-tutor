package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预警日志实体（表 alert_log）：教师查看学生预警记录
 */
@Getter
@Entity
@Table(name = "alert_log")
@NoArgsConstructor
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(nullable = false, length = 512)
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "tinyint")
    private boolean read;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AlertLog(Long teacherId, Long classId, Long studentId, Long ruleId, String message) {
        this.teacherId = teacherId;
        this.classId = classId;
        this.studentId = studentId;
        this.ruleId = ruleId;
        this.message = message;
        this.read = false;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
