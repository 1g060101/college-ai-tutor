package com.aistudy.tutor.domain.subject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 导入任务实体（表 import_job），记录文件导入进度与结果
 */
@Getter
@Entity
@Table(name = "import_job")
@NoArgsConstructor
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /** 导入类型：COURSE / QUESTION */
    @Column(name = "job_type", nullable = false, length = 20)
    private String jobType;

    /** 任务状态：PENDING / RUNNING / DONE / FAILED */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "success_count", nullable = false)
    private int successCount;

    @Column(name = "fail_count", nullable = false)
    private int failCount;

    @Column(name = "error_msg", length = 512)
    private String errorMsg;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ImportJob(Long userId, String fileName, String jobType, String status) {
        this.userId = userId;
        this.fileName = fileName;
        this.jobType = jobType;
        this.status = status;
        this.deleted = false;
    }

    /** 全部处理完成：写入统计并置为 DONE */
    public void markDone(int totalCount, int successCount, int failCount) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.status = "DONE";
    }

    /** 整体失败（如无内容可导入） */
    public void markFailed(String errorMsg) {
        this.status = "FAILED";
        this.errorMsg = errorMsg;
    }

    /** 仅记录首条行解析错误 */
    public void recordFirstError(String errorMsg) {
        if (this.errorMsg == null && errorMsg != null && !errorMsg.isBlank()) {
            this.errorMsg = errorMsg;
        }
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
