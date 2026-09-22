-- V3: 题目与作答事件（功能域 04 作业辅导）
CREATE TABLE IF NOT EXISTS `question` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`          BIGINT       NOT NULL,
    `knowledge_point_id` BIGINT       NULL,
    `type`               VARCHAR(20)  NOT NULL COMMENT 'CHOICE/FILL/BLANK/SUBJECTIVE...',
    `content`            TEXT         NOT NULL,
    `options`            JSON         NULL COMMENT '选择题选项',
    `answer`             TEXT         NULL,
    `analysis`           TEXT         NULL COMMENT '答案解析',
    `difficulty`         TINYINT      NOT NULL DEFAULT 3,
    `source`             VARCHAR(64)  NULL COMMENT '题目来源：作业/真题/模拟卷',
    `is_deleted`         TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_q_course` (`course_id`),
    KEY `idx_q_kp` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `answer_event` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `request_id`       VARCHAR(64)  NOT NULL COMMENT '幂等键',
    `user_id`          BIGINT       NOT NULL,
    `question_id`      BIGINT       NOT NULL,
    `course_id`        BIGINT       NULL,
    `knowledge_point_id` BIGINT     NULL COMMENT '命中知识点 kp_hit',
    `answer_content`   TEXT         NULL,
    `correct`          TINYINT(1)   NOT NULL COMMENT '是否答对',
    `score`            DECIMAL(5,2) NULL COMMENT '得分',
    `duration_seconds` INT          NULL COMMENT '作答耗时',
    `source`           VARCHAR(20)  NOT NULL DEFAULT 'HW' COMMENT 'HOMEWORK/EXAM/QA/STUDY',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_answer_request` (`request_id`),
    KEY `idx_ae_user` (`user_id`),
    KEY `idx_ae_kp` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;