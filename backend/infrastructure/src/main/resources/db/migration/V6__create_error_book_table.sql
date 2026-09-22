-- V6: 错题本（功能域 04 作业辅导）
CREATE TABLE IF NOT EXISTS `error_book` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL,
    `question_id`    BIGINT       NOT NULL,
    `course_id`      BIGINT       NULL,
    `knowledge_point_id` BIGINT   NULL,
    `ped`            VARCHAR(128) NULL COMMENT '题目指纹，按 ped 去重合并',
    `wrong_answer`   TEXT         NULL,
    `error_reason`   VARCHAR(255) NULL,
    `error_count`    INT          NOT NULL DEFAULT 1,
    `mastered`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已掌握',
    `last_wrong_at`  DATETIME     NULL,
    `is_deleted`     TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_error_user_question` (`user_id`, `question_id`),
    KEY `idx_eb_kp` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;