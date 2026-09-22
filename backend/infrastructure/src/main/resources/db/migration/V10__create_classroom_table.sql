-- V10: 课堂辅助功能域（功能域 06 课堂辅助能力）
CREATE TABLE IF NOT EXISTS `parsed_material` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL,
    `course_id`    BIGINT       NULL,
    `file_name`    VARCHAR(255) NOT NULL,
    `file_type`    VARCHAR(20)  NOT NULL COMMENT 'PPT/PDF/WORD/MD',
    `content`      MEDIUMTEXT   NULL COMMENT '解析后的文本',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PARSING/DONE/FAILED',
    `error_msg`    VARCHAR(512) NULL,
    `is_deleted`   TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_pm_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `transcript` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL,
    `course_id`    BIGINT       NULL,
    `lecture_name` VARCHAR(128) NULL,
    `content`      MEDIUMTEXT   NOT NULL COMMENT '转写原文',
    `expire_at`    DATETIME     NOT NULL COMMENT '30天过期',
    `is_deleted`   TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ts_user` (`user_id`),
    KEY `idx_ts_expire` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `summary` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       NOT NULL,
    `source_type`   VARCHAR(20)  NOT NULL COMMENT 'MATERIAL/TRANSCRIPT',
    `source_id`     BIGINT       NOT NULL,
    `content`       MEDIUMTEXT   NOT NULL COMMENT '提炼总结',
    `is_deleted`    TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sum_user` (`user_id`),
    KEY `idx_sum_source` (`source_type`, `source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;