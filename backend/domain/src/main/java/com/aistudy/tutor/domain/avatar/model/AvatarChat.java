package com.aistudy.tutor.domain.avatar.model;

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
 * 数字人陪伴对话记录实体（表 avatar_chat，无软删字段）
 */
@Getter
@Entity
@Table(name = "avatar_chat")
@NoArgsConstructor
public class AvatarChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 角色：USER/AVATAR */
    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 情绪反馈 */
    @Column(length = 20)
    private String emotion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AvatarChat(Long userId, String role, String content, String emotion) {
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.emotion = emotion;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
