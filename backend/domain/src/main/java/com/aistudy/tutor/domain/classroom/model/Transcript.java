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
 * 课堂录音转写实体（表 transcript）
 */
@Getter
@Entity
@Table(name = "transcript")
@NoArgsConstructor
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "lecture_name", length = 128)
    private String lectureName;

    /** 转写原文 */
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    /** 过期时间：转写后 30 天 */
    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Transcript(Long userId, Long courseId, String lectureName, String content, LocalDateTime expireAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.lectureName = lectureName;
        this.content = content;
        this.expireAt = expireAt;
        this.deleted = false;
    }

    /**
     * 软删除（过期录音清理）
     */
    public void softDelete() {
        this.deleted = true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
