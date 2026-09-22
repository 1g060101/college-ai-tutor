package com.aistudy.tutor.domain.qa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 答疑会话实体（表 qa_session）
 */
@Getter
@Entity
@Table(name = "qa_session")
@NoArgsConstructor
public class QaSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(nullable = false, length = 128)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QaSessionStatus status = QaSessionStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "reply_mode", nullable = false, length = 20)
    private ReplyMode replyMode = ReplyMode.GUIDED;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public QaSession(Long userId, Long courseId, String title, ReplyMode replyMode) {
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.replyMode = replyMode;
        this.deleted = false;
    }

    /**
     * 关闭会话
     */
    public void close() {
        this.status = QaSessionStatus.CLOSED;
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
