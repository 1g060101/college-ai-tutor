package com.aistudy.tutor.domain.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户知识点掌握度聚合：EWMA 重算结果落库，支撑学习报告与同类题推荐。
 */
@Getter
@Setter
@Entity
@Table(name = "user_knowledge_point")
@NoArgsConstructor
public class UserKnowledgePoint {

    @EmbeddedId
    private UserKnowledgePointId id;

    /** 掌握度 0-100 */
    @Column(name = "mastery_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal masteryScore = BigDecimal.ZERO;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    @Column(name = "last_answered_at")
    private LocalDateTime lastAnsweredAt;

    @Column(name = "recalc_ts")
    private LocalDateTime recalcTs;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserKnowledgePoint(UserKnowledgePointId id) {
        this.id = id;
        this.deleted = false;
    }

    /**
     * 用最新重算结果更新掌握度与计数
     */
    public void updateMastery(BigDecimal score, int attempt, int correct, LocalDateTime answeredAt) {
        this.masteryScore = score;
        this.attemptCount = attempt;
        this.correctCount = correct;
        this.lastAnsweredAt = answeredAt;
        this.updatedAt = LocalDateTime.now();
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
