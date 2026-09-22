package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 班级实体（表 class）：教师创建班级，学生凭邀请码加入
 */
@Getter
@Entity
@Table(name = "class")
@NoArgsConstructor
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 64)
    private String subject;

    @Column(name = "invite_code", nullable = false, length = 32)
    private String inviteCode;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SchoolClass(Long teacherId, String name, String subject, String inviteCode) {
        this.teacherId = teacherId;
        this.name = name;
        this.subject = subject;
        this.inviteCode = inviteCode;
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
