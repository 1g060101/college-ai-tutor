-- V12: 多学科全覆盖（功能域 09 多学科/课程导入）
CREATE TABLE IF NOT EXISTS `subject_template` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `subject`        VARCHAR(64)  NOT NULL,
    `template_name`  VARCHAR(128) NOT NULL,
    `fields`         JSON         NULL COMMENT '字段映射模板',
    `is_deleted`     TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subject_template` (`subject`, `template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `field_mapping` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `template_id` BIGINT       NOT NULL,
    `source_field` VARCHAR(64) NOT NULL COMMENT '导入文件字段名',
    `target_field` VARCHAR(64) NOT NULL COMMENT '系统字段名',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_fm_template` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `import_job` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL,
    `file_name`    VARCHAR(255) NOT NULL,
    `job_type`     VARCHAR(20)  NOT NULL COMMENT 'COURSE/QUESTION',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RUNNING/DONE/FAILED',
    `total_count`  INT          NOT NULL DEFAULT 0,
    `success_count` INT         NOT NULL DEFAULT 0,
    `fail_count`   INT          NOT NULL DEFAULT 0,
    `error_msg`    VARCHAR(512) NULL,
    `is_deleted`   TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_import_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 学习路径（功能域 02 课程导学，DAG 生成）
CREATE TABLE IF NOT EXISTS `learning_path` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`   BIGINT       NOT NULL,
    `user_id`     BIGINT       NULL COMMENT '空=通用路径',
    `name`        VARCHAR(128) NOT NULL,
    `node_order`  JSON         NOT NULL COMMENT '拓扑排序后的知识点顺序',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_lp_course` (`course_id`),
    KEY `idx_lp_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;