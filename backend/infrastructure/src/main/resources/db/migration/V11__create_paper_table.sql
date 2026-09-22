-- V11: 备考冲刺服务（功能域 07 备考冲刺）
CREATE TABLE IF NOT EXISTS `paper` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`   BIGINT       NOT NULL,
    `title`       VARCHAR(128) NOT NULL,
    `paper_type`  VARCHAR(20)  NOT NULL DEFAULT 'MOCK' COMMENT 'REAL/MOCK',
    `year`        INT          NULL,
    `duration_minutes` INT     NULL,
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_paper_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `paper_question` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `paper_id`    BIGINT      NOT NULL,
    `question_id` BIGINT      NOT NULL,
    `sort_order`  INT         NOT NULL DEFAULT 0,
    `score`       DECIMAL(5,2) NOT NULL DEFAULT 0,
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0,
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_pq_paper` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `hot_kp_rank` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`   BIGINT       NOT NULL,
    `knowledge_point_id` BIGINT NOT NULL,
    `exam_freq`   INT          NOT NULL DEFAULT 0 COMMENT '考频',
    `avg_score`   DECIMAL(5,2) NULL,
    `rank_date`   DATE         NOT NULL COMMENT '榜单日期',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_hot_course_date` (`course_id`, `rank_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;