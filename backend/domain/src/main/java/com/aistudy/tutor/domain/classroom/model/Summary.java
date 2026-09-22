package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 要点提炼实体（表 summary），来源可为课件（MATERIAL）或录音转写（TRANSCRIPT）
 */
@Getter
@Entity
@Table(name = "summary")
@NoArgsConstructor
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 来源类型：MATERIAL / TRANSCRIPT */
    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    /** 提炼总结内容 */
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Summary(Long userId, String sourceType, Long sourceId, String content) {
        this.userId = userId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.content = content;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
