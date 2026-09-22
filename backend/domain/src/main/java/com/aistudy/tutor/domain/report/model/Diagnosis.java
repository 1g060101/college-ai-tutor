package com.aistudy.tutor.domain.report.model;

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
 * 学习诊断记录：周期诊断结论、薄弱点与建议（JSON 字符串落库）。
 */
@Getter
@Entity
@Table(name = "diagnosis")
@NoArgsConstructor
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 周期：WEEK / MONTH */
    @Column(length = 10, nullable = false)
    private String period;

    @Column(columnDefinition = "TEXT")
    private String summary;

    /** 薄弱知识点 JSON 字符串 */
    @Column(name = "weak_points", columnDefinition = "json")
    private String weakPoints;

    /** 建议 JSON 字符串 */
    @Column(columnDefinition = "json")
    private String suggestions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Diagnosis(Long userId, String period, String summary, String weakPoints, String suggestions) {
        this.userId = userId;
        this.period = period;
        this.summary = summary;
        this.weakPoints = weakPoints;
        this.suggestions = suggestions;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
