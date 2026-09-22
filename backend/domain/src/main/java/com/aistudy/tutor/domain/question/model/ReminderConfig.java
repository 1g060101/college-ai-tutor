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
 * 截止/完成度提醒配置实体（表 reminder_config）：每用户一行（uk_reminder_user 唯一）
 */
@Getter
@Entity
@Table(name = "reminder_config")
@NoArgsConstructor
public class ReminderConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 是否开启提醒 */
    @Column(nullable = false, columnDefinition = "tinyint")
    private boolean enabled = true;

    /** 提前提醒小时数，默认 24 */
    @Column(name = "remind_before_hours", nullable = false)
    private int remindBeforeHours = 24;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ReminderConfig(Long userId, boolean enabled, int remindBeforeHours) {
        this.userId = userId;
        this.enabled = enabled;
        this.remindBeforeHours = remindBeforeHours;
    }

    /**
     * 更新提醒配置
     */
    public void update(boolean enabled, int remindBeforeHours) {
        this.enabled = enabled;
        this.remindBeforeHours = remindBeforeHours;
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
