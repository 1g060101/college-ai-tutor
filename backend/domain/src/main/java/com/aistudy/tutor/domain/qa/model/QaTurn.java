package com.aistudy.tutor.domain.qa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 答疑单轮对话实体（表 qa_turn）
 */
@Getter
@Entity
@Table(name = "qa_turn")
@NoArgsConstructor
public class QaTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "kp_hit")
    private Long kpHit;

    @Column(name = "guidance_level", length = 10)
    private String guidanceLevel;

    @Column(length = 255)
    private String feedback;

    @Column(name = "reply_mode", nullable = false, length = 20)
    private String replyMode = "GUIDED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public QaTurn(Long sessionId, Long userId, String question, String answer, Long kpHit,
                  String guidanceLevel, String feedback, String replyMode) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.kpHit = kpHit;
        this.guidanceLevel = guidanceLevel;
        this.feedback = feedback;
        this.replyMode = replyMode;
    }

    /**
     * 记录用户反馈文本
     */
    public void markFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * 设置命中的知识点 id（便于回写掌握度）
     */
    public void setKpHit(Long kpHit) {
        this.kpHit = kpHit;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
