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
 * 计划任务项实体（表 task_items）
 */
@Getter
@Entity
@Table(name = "task_items")
@NoArgsConstructor
public class TaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    @Column(nullable = false, length = 128)
    private String title;

    /** 任务类型：REVIEW/PREVIEW/RECITE/DICTATION/FORMULA */
    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType;

    /** 状态：TODO/DONE/SKIPPED */
    @Column(nullable = false, length = 20)
    private String status = "TODO";

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    /** 记忆曲线调度：下次复习日期 */
    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    /** 记忆曲线调度：当前阶段（复习间隔天数按阶段推算） */
    @Column(name = "repeat_interval")
    private Integer repeatInterval;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TaskItem(Long planId, Long userId, Long knowledgePointId, String title, String taskType, LocalDate scheduledDate) {
        this.planId = planId;
        this.userId = userId;
        this.knowledgePointId = knowledgePointId;
        this.title = title;
        this.taskType = taskType;
        this.scheduledDate = scheduledDate;
        this.deleted = false;
    }

    /**
     * 标记任务完成
     */
    public void markDone() {
        this.status = "DONE";
    }

    /**
     * 艾宾浩斯推进：写回当前阶段与下次复习日期
     */
    public void advanceReview(int stage, int intervalDays, LocalDate nextReviewDate) {
        this.repeatInterval = stage;
        this.nextReviewDate = nextReviewDate;
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
