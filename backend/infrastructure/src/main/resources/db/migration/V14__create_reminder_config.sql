-- V14: 提醒配置（功能域 04 作业辅导-截止/完成度提醒）
CREATE TABLE IF NOT EXISTS `reminder_config` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`              BIGINT       NOT NULL,
    `enabled`              TINYINT(1)   NOT NULL DEFAULT 1,
    `remind_before_hours`  INT          NOT NULL DEFAULT 24,
    `created_at`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reminder_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
