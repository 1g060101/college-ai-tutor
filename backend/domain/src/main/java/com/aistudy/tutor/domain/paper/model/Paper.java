package com.aistudy.tutor.domain.paper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 试卷实体（表 paper）：真题/模拟卷上传后挂载题目
 */
@Getter
@Entity
@Table(name = "paper")
@NoArgsConstructor
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(nullable = false, length = 128)
    private String title;

    /** 卷型：REAL 真题 / MOCK 模拟卷 */
    @Column(name = "paper_type", nullable = false, length = 20)
    private String paperType;

    @Column
    private Integer year;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Paper(Long courseId, String title, String paperType, Integer year, Integer durationMinutes) {
        this.courseId = courseId;
        this.title = title;
        this.paperType = paperType;
        this.year = year;
        this.durationMinutes = durationMinutes;
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
