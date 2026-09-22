package com.aistudy.tutor.domain.subject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字段映射实体（表 field_mapping），导入文件源字段 → 系统目标字段
 */
@Getter
@Entity
@Table(name = "field_mapping")
@NoArgsConstructor
public class FieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "source_field", nullable = false, length = 64)
    private String sourceField;

    @Column(name = "target_field", nullable = false, length = 64)
    private String targetField;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FieldMapping(String sourceField, String targetField) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.deleted = false;
    }

    /** 模板保存后回填 template_id */
    public void bindTemplate(Long templateId) {
        this.templateId = templateId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
