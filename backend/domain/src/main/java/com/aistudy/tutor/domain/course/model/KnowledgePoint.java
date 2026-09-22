package com.aistudy.tutor.domain.course.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 知识点实体（表 knowledge_point），供答疑 kp_hit 匹配与掌握度回写
 */
@Getter
@Entity
@Table(name = "knowledge_point")
@NoArgsConstructor
public class KnowledgePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(length = 255)
    private String tags;

    @Column(nullable = false, columnDefinition = "tinyint")
    private int difficulty = 3;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight = new BigDecimal("1.00");

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
