package com.aistudy.tutor.domain.avatar.model;

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
 * 数字人形象配置实体（表 avatar_config，按 user_id 唯一）
 */
@Getter
@Entity
@Table(name = "avatar_config")
@NoArgsConstructor
public class AvatarConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "avatar_name", nullable = false, length = 64)
    private String avatarName = "小灵";

    /** 语气：FRIENDLY/STRICT/CASUAL */
    @Column(nullable = false, length = 20)
    private String tone = "FRIENDLY";

    /** 风格：CONCISE/DETAILED/MOTIVATIONAL */
    @Column(nullable = false, length = 20)
    private String style = "CONCISE";

    /** 音色 */
    @Column(length = 20)
    private String voice;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AvatarConfig(Long userId, String avatarName, String tone, String style, String voice) {
        this.userId = userId;
        this.avatarName = avatarName;
        this.tone = tone;
        this.style = style;
        this.voice = voice;
        this.deleted = false;
    }

    /**
     * 更新配置：非空字段覆盖（voice 允许置空）
     */
    public void update(String avatarName, String tone, String style, String voice) {
        if (avatarName != null && !avatarName.isBlank()) {
            this.avatarName = avatarName;
        }
        if (tone != null && !tone.isBlank()) {
            this.tone = tone;
        }
        if (style != null && !style.isBlank()) {
            this.style = style;
        }
        if (voice != null) {
            this.voice = voice;
        }
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
