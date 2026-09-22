package com.aistudy.tutor.domain.subject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学科模板实体（表 subject_template），fields 存字段映射 JSON 列表 [{sourceField,targetField}]
 */
@Getter
@Entity
@Table(name = "subject_template")
@NoArgsConstructor
public class SubjectTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String subject;

    @Column(name = "template_name", nullable = false, length = 128)
    private String templateName;

    /** 字段映射模板 JSON：源字段 → 目标字段 列表 */
    @Column(columnDefinition = "json")
    private String fields;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SubjectTemplate(String subject, String templateName, String fields) {
        this.subject = subject;
        this.templateName = templateName;
        this.fields = fields;
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
