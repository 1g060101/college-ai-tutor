package com.aistudy.tutor.domain.course.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习路径实体（表 learning_path），node_order 存知识点 id 的拓扑排序 JSON 列表
 */
@Getter
@Entity
@Table(name = "learning_path")
@NoArgsConstructor
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /** 空表示通用路径（所有用户可见），否则为该用户私有路径 */
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 128)
    private String name;

    /** 知识点 id 有序列表 JSON，如 [3,1,2] */
    @Column(name = "node_order", nullable = false, columnDefinition = "json")
    private String nodeOrder;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LearningPath(Long courseId, Long userId, String name, String nodeOrder) {
        this.courseId = courseId;
        this.userId = userId;
        this.name = name;
        this.nodeOrder = nodeOrder;
        this.deleted = false;
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
