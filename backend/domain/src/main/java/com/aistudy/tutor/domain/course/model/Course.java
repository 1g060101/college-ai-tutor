package com.aistudy.tutor.domain.course.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程实体（表 course），课程知识智能导学的基础数据
 */
@Getter
@Entity
@Table(name = "course")
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 64)
    private String subject;

    @Column(length = 512)
    private String description;

    @Column(name = "cover_url", length = 512)
    private String coverUrl;

    /** 难度 1-5，默认 3 */
    @Column(nullable = false, columnDefinition = "tinyint")
    private int difficulty;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Course(String name, String subject, String description, String coverUrl, int difficulty) {
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.coverUrl = coverUrl;
        this.difficulty = difficulty;
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
