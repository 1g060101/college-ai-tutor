-- V2: 课程与知识点（功能域 02 课程导学）
CREATE TABLE IF NOT EXISTS `course` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(128) NOT NULL COMMENT '课程名称',
    `subject`     VARCHAR(64)  NOT NULL COMMENT '学科',
    `description` VARCHAR(512) NULL COMMENT '课程描述',
    `cover_url`   VARCHAR(512) NULL,
    `difficulty`  TINYINT      NOT NULL DEFAULT 3 COMMENT '难度 1-5',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_course_subject` (`subject`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `knowledge_point` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`   BIGINT       NOT NULL,
    `parent_id`   BIGINT       NULL COMMENT '父知识点，树形结构',
    `name`        VARCHAR(128) NOT NULL,
    `description` VARCHAR(512) NULL,
    `tags`        VARCHAR(255) NULL COMMENT '重难点/考点/易错点，逗号分隔',
    `difficulty`  TINYINT      NOT NULL DEFAULT 3,
    `weight`      DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '考频权重',
    `sort_order`  INT          NOT NULL DEFAULT 0,
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_kp_course` (`course_id`),
    KEY `idx_kp_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;