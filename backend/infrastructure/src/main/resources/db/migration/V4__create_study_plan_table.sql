-- V4: 预习复习规划与任务项（功能域 05 预习复习）
CREATE TABLE IF NOT EXISTS `study_plan` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `course_id`   BIGINT       NULL,
    `title`       VARCHAR(128) NOT NULL,
    `period`      VARCHAR(20)  NOT NULL DEFAULT 'WEEK' COMMENT 'WEEK/MONTH/EXAM',
    `start_date`  DATE         NULL,
    `end_date`    DATE         NULL,
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DONE/CANCELED',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sp_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `task_items` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `plan_id`           BIGINT       NOT NULL,
    `user_id`           BIGINT       NOT NULL,
    `knowledge_point_id` BIGINT      NULL,
    `title`             VARCHAR(128) NOT NULL,
    `task_type`         VARCHAR(20)  NOT NULL COMMENT 'REVIEW/PREVIEW/RECITE/DICTATION/FORMULA',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'TODO' COMMENT 'TODO/DONE/SKIPPED',
    `scheduled_date`    DATE         NULL,
    `next_review_date`  DATE         NULL COMMENT '记忆曲线调度',
    `repeat_interval`   INT          NULL COMMENT '复习间隔天数',
    `is_deleted`        TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ti_plan` (`plan_id`),
    KEY `idx_ti_user_date` (`user_id`, `scheduled_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `study_record` (
    `id`             BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT      NOT NULL,
    `course_id`      BIGINT      NULL,
    `knowledge_point_id` BIGINT  NULL,
    `duration_seconds` INT       NOT NULL DEFAULT 0,
    `record_type`    VARCHAR(20) NOT NULL DEFAULT 'STUDY' COMMENT 'STUDY/REVIEW/EXAM',
    `record_date`    DATE        NOT NULL,
    `created_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sr_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;