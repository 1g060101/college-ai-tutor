package com.aistudy.tutor.domain.question.model;

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

import java.time.LocalDateTime;

/**
 * 错题本实体（表 error_book）：同一用户同一题目唯一（按 user_id + question_id upsert 累加错误次数）
 */
@Getter
@Entity
@Table(name = "error_book")
@NoArgsConstructor
public class ErrorBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    /** 题目指纹（幂等键），创建时取 requestId */
    @Column(length = 128)
    private String ped;

    /** 错误作答内容 */
    @Column(name = "wrong_answer", columnDefinition = "TEXT")
    private String wrongAnswer;

    @Column(name = "error_reason", length = 255)
    private String errorReason;

    /** 累计错误次数 */
    @Column(name = "error_count", nullable = false)
    private int errorCount = 1;

    /** 是否已掌握（注意表列名是 mastered） */
    @Column(name = "mastered", nullable = false)
    private boolean mastered;

    @Column(name = "last_wrong_at")
    private LocalDateTime lastWrongAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ErrorBook(Long userId, Long questionId, Long courseId, Long knowledgePointId,
                     String ped, String wrongAnswer, String errorReason) {
        this.userId = userId;
        this.questionId = questionId;
        this.courseId = courseId;
        this.knowledgePointId = knowledgePointId;
        this.ped = ped;
        this.wrongAnswer = wrongAnswer;
        this.errorReason = errorReason;
        this.errorCount = 1;
        this.mastered = false;
        this.lastWrongAt = LocalDateTime.now();
        this.deleted = false;
    }

    /**
     * 再次做错：错误次数 +1、刷新最近错误时间、重置掌握状态
     */
    public void markWrongAgain() {
        this.errorCount++;
        this.lastWrongAt = LocalDateTime.now();
        this.mastered = false;
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
