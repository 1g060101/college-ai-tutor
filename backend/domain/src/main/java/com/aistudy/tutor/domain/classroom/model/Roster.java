package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 班级花名册实体（表 roster）：学生加入班级记录
 */
@Getter
@Entity
@Table(name = "roster")
@NoArgsConstructor
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "join_time", nullable = false)
    private LocalDateTime joinTime;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Roster(Long classId, Long studentId) {
        this.classId = classId;
        this.studentId = studentId;
        this.joinTime = LocalDateTime.now();
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
