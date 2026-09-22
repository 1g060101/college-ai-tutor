package com.aistudy.tutor.domain.paper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 高频考点排行实体（表 hot_kp_rank）：按日统计各知识点考察频次与平均得分
 */
@Getter
@Entity
@Table(name = "hot_kp_rank")
@NoArgsConstructor
public class HotKpRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "knowledge_point_id", nullable = false)
    private Long knowledgePointId;

    @Column(name = "exam_freq", nullable = false)
    private int examFreq;

    @Column(name = "avg_score", precision = 5, scale = 2)
    private BigDecimal avgScore;

    @Column(name = "rank_date", nullable = false)
    private LocalDate rankDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public HotKpRank(Long courseId, Long knowledgePointId, int examFreq, BigDecimal avgScore, LocalDate rankDate) {
        this.courseId = courseId;
        this.knowledgePointId = knowledgePointId;
        this.examFreq = examFreq;
        this.avgScore = avgScore;
        this.rankDate = rankDate;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
