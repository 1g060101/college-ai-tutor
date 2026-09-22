package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作业实体（表 assignment）：教师向班级发布作业
 */
@Getter
@Entity
@Table(name = "assignment")
@NoArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Assignment(Long classId, Long teacherId, String title, String description, LocalDateTime dueDate) {
        this.classId = classId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
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
