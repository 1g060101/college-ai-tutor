package com.aistudy.tutor.domain.classroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 解析后的课件/教材实体（表 parsed_material）
 */
@Getter
@Entity
@Table(name = "parsed_material")
@NoArgsConstructor
public class ParsedMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /** 文件类型：PPT / PDF / WORD / MD */
    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType;

    /** 解析后的纯文本内容 */
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    /** 解析状态：PENDING / PARSING / DONE / FAILED */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "error_msg", length = 512)
    private String errorMsg;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ParsedMaterial(Long userId, Long courseId, String fileName, String fileType,
                          String content, String status, String errorMsg) {
        this.userId = userId;
        this.courseId = courseId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
        this.status = status;
        this.errorMsg = errorMsg;
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
