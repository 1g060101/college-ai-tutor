-- V9: 答疑会话与多轮追问（功能域 03 AI 智能答疑·核心）
CREATE TABLE IF NOT EXISTS `qa_session` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `course_id`   BIGINT       NULL,
    `title`       VARCHAR(128) NOT NULL,
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/CLOSED',
    `reply_mode`  VARCHAR(20)  NOT NULL DEFAULT 'GUIDED' COMMENT 'GUIDED 引导式/DIRECT 直接回答',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_qa_session_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `qa_turn` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `session_id`    BIGINT      NOT NULL,
    `user_id`       BIGINT      NOT NULL,
    `question`      TEXT        NOT NULL,
    `answer`        TEXT        NULL COMMENT '引导式分段输出：思路→分级提示→总结',
    `kp_hit`        BIGINT      NULL COMMENT '命中的知识点',
    `guidance_level` VARCHAR(10) NULL COMMENT 'HINT1/HINT2/HINT3',
    `feedback`      VARCHAR(255) NULL COMMENT '用户反馈，回写掌握度',
    `reply_mode`    VARCHAR(20) NOT NULL DEFAULT 'GUIDED',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_qa_turn_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI 调用审计日志（网关解耦必建）
CREATE TABLE IF NOT EXISTS `ai_call_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `request_id`  VARCHAR(64)  NOT NULL,
    `user_id`     BIGINT       NULL,
    `channel`     VARCHAR(20)  NOT NULL COMMENT 'DEEPSEEK/OCR/ASR/TTS',
    `model`       VARCHAR(64)  NULL,
    `tokens_in`   INT          NULL,
    `tokens_out`  INT          NULL,
    `cost`        DECIMAL(10,4) NULL,
    `success`     TINYINT(1)   NOT NULL DEFAULT 1,
    `error_msg`   VARCHAR(512) NULL,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ai_log_user` (`user_id`),
    KEY `idx_ai_log_request` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;