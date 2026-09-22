-- V7: 班级/名单/作业发布/预警（功能域 10 教师后台）
CREATE TABLE IF NOT EXISTS `class` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `teacher_id`  BIGINT       NOT NULL,
    `name`        VARCHAR(128) NOT NULL,
    `subject`     VARCHAR(64)  NULL,
    `invite_code` VARCHAR(16)  NULL COMMENT '学生加入班级码',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_class_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `roster` (
    `id`         BIGINT     NOT NULL AUTO_INCREMENT,
    `class_id`   BIGINT     NOT NULL,
    `student_id` BIGINT     NOT NULL,
    `join_time`  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_roster_class_student` (`class_id`, `student_id`),
    KEY `idx_roster_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `assignment` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `class_id`    BIGINT       NOT NULL,
    `teacher_id`  BIGINT       NOT NULL,
    `title`       VARCHAR(128) NOT NULL,
    `description` TEXT         NULL,
    `due_date`    DATETIME     NULL,
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_assign_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `warn_rule` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `teacher_id` BIGINT       NOT NULL,
    `rule_type`  VARCHAR(20)  NOT NULL COMMENT 'FREQUENCY/MASTERY/ABSENCE',
    `threshold`  DECIMAL(8,2) NOT NULL,
    `description` VARCHAR(255) NULL,
    `is_deleted` TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_warn_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `alert_log` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `teacher_id` BIGINT      NOT NULL,
    `class_id`   BIGINT      NULL,
    `student_id` BIGINT      NULL,
    `rule_id`    BIGINT      NULL,
    `message`    VARCHAR(512) NOT NULL,
    `is_read`    TINYINT(1)  NOT NULL DEFAULT 0,
    `is_deleted` TINYINT(1)  NOT NULL DEFAULT 0,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_alert_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;