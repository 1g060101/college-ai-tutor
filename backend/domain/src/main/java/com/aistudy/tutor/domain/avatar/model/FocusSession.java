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
import java.time.temporal.ChronoUnit;

/**
 * 专注计时会话实体（表 focus_session）
 */
@Getter
@Entity
@Table(name = "focus_session")
@NoArgsConstructor
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    /** 状态：RUNNING/DONE/ABANDONED */
    @Column(nullable = false, length = 20)
    private String status = "RUNNING";

    /** 是否已提醒疲劳 */
    @Column(name = "fatigue_notified", nullable = false, columnDefinition = "tinyint")
    private boolean fatigueNotified;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * @param durationMinutes 预估专注时长（分钟），可空
     */
    public FocusSession(Long userId, Long courseId, LocalDateTime startTime, Integer durationMinutes) {
        this.userId = userId;
        this.courseId = courseId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes == null ? 0 : durationMinutes;
        this.deleted = false;
    }

    /**
     * 结束专注：优先使用前台校准时长，否则按 start_time 到 end_time 计算分钟数
     */
    public void finish(LocalDateTime endTime, Integer frontendMinutes) {
        this.endTime = endTime;
        if (frontendMinutes != null && frontendMinutes > 0) {
            this.durationMinutes = frontendMinutes;
        } else {
            long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
            this.durationMinutes = (int) Math.max(0, minutes);
        }
        this.status = "DONE";
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
