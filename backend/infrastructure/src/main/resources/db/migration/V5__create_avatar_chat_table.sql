-- V5: 数字人陪伴（功能域 01 数字人学习陪伴）
CREATE TABLE IF NOT EXISTS `avatar_config` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `avatar_name` VARCHAR(64)  NOT NULL DEFAULT '小灵',
    `tone`        VARCHAR(20)  NOT NULL DEFAULT 'FRIENDLY' COMMENT '语气 FRIENDLY/STRICT/CASUAL',
    `style`       VARCHAR(20)  NOT NULL DEFAULT 'CONCISE' COMMENT '风格 CONCISE/DETAILED/MOTIVATIONAL',
    `voice`       VARCHAR(20)  NULL COMMENT '音色',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_avatar_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `focus_session` (
    `id`               BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`          BIGINT      NOT NULL,
    `course_id`        BIGINT      NULL,
    `start_time`       DATETIME    NOT NULL,
    `end_time`         DATETIME    NULL,
    `duration_minutes` INT         NOT NULL DEFAULT 0,
    `status`           VARCHAR(20) NOT NULL DEFAULT 'RUNNING' COMMENT 'RUNNING/DONE/ABANDONED',
    `fatigue_notified` TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否已提醒疲劳',
    `is_deleted`       TINYINT(1)  NOT NULL DEFAULT 0,
    `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_fs_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `avatar_chat` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT      NOT NULL,
    `role`       VARCHAR(10) NOT NULL COMMENT 'USER/AVATAR',
    `content`    TEXT        NOT NULL,
    `emotion`    VARCHAR(20) NULL COMMENT '情绪反馈',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ac_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;