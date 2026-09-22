package com.aistudy.tutor.domain.question.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作答事件：记录一次题目作答 / 答疑回写，是掌握度重算的原始输入。
 */
@Getter
@Entity
@Table(name = "answer_event")
@NoArgsConstructor
public class AnswerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 幂等键：同一请求不重复入库 */
    @Column(name = "request_id", nullable = false, unique = true, length = 64)
    private String requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 题目 id：QA 答疑回写时传哨兵值 0 */
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String answerContent;

    @Column(nullable = false)
    private boolean correct;

    /** 得分 0-100（百分制） */
    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    /** 来源：HOMEWORK / EXAM / QA / STUDY */
    @Column(length = 20, nullable = false)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AnswerEvent(Long userId, Long questionId, Long courseId, Long knowledgePointId,
                       String answerContent, boolean correct, BigDecimal score,
                       Integer durationSeconds, String source, String requestId) {
        this.userId = userId;
        this.questionId = questionId;
        this.courseId = courseId;
        this.knowledgePointId = knowledgePointId;
        this.answerContent = answerContent;
        this.correct = correct;
        this.score = score;
        this.durationSeconds = durationSeconds;
        this.source = source;
        this.requestId = requestId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
