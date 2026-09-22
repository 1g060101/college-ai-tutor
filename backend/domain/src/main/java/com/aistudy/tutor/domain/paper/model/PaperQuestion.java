package com.aistudy.tutor.domain.paper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 试卷题目关联实体（表 paper_question）：记录题号（sort_order）与分值
 */
@Getter
@Entity
@Table(name = "paper_question")
@NoArgsConstructor
public class PaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PaperQuestion(Long paperId, Long questionId, int sortOrder, BigDecimal score) {
        this.paperId = paperId;
        this.questionId = questionId;
        this.sortOrder = sortOrder;
        this.score = score;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
