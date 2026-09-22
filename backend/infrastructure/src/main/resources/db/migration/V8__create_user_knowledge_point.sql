-- V8: 用户-知识点掌握度主表（功能域 08 学习数据与成长报告）
CREATE TABLE IF NOT EXISTS `user_knowledge_point` (
    `user_id`         BIGINT       NOT NULL,
    `knowledge_point_id` BIGINT    NOT NULL,
    `mastery_score`   DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '掌握度 0-100',
    `attempt_count`   INT          NOT NULL DEFAULT 0,
    `correct_count`   INT          NOT NULL DEFAULT 0,
    `last_answered_at` DATETIME    NULL,
    `recalc_ts`       DATETIME     NULL COMMENT '异步增量重算时间戳',
    `is_deleted`      TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `knowledge_point_id`),
    KEY `idx_ukp_kp` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 学习时长聚合表（周/月报告）
CREATE TABLE IF NOT EXISTS `study_minutes_agg` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT      NOT NULL,
    `course_id`   BIGINT      NULL,
    `stat_date`   DATE        NOT NULL,
    `study_minutes` INT       NOT NULL DEFAULT 0,
    `focus_minutes` INT       NOT NULL DEFAULT 0,
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_sma_user_date_course` (`user_id`, `stat_date`, `course_id`),
    PRIMARY KEY (`id`),
    KEY `idx_sma_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 学习诊断记录（周/月诊断，带可解释原因）
CREATE TABLE IF NOT EXISTS `diagnosis` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `period`      VARCHAR(10)  NOT NULL COMMENT 'WEEK/MONTH',
    `summary`     TEXT         NULL,
    `weak_points` JSON         NULL COMMENT '薄弱知识点及原因',
    `suggestions` JSON         NULL COMMENT '建议列表',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_diag_user_period` (`user_id`, `period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;