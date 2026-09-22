package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警规则实体（表 warn_rule）：教师自定义预警阈值（FREQUENCY/MASTERY/ABSENCE）
 */
@Getter
@Entity
@Table(name = "warn_rule")
@NoArgsConstructor
public class WarnRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /** 规则类型：FREQUENCY 学习频次 / MASTERY 掌握度 / ABSENCE 缺勤 */
    @Column(name = "rule_type", nullable = false, length = 20)
    private String ruleType;

    @Column(precision = 8, scale = 2)
    private BigDecimal threshold;

    @Column(length = 255)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WarnRule(Long teacherId, String ruleType, BigDecimal threshold, String description) {
        this.teacherId = teacherId;
        this.ruleType = ruleType;
        this.threshold = threshold;
        this.description = description;
        this.deleted = false;
    }

    /** 更新阈值（保留同一规则配置） */
    public void updateThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
