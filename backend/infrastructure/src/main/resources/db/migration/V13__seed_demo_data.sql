-- V13: 演示种子数据（课程 / 知识点 / 题目），支撑「答疑→掌握度→报告→推荐」闭环演示
INSERT INTO `course` (`name`, `subject`, `description`, `difficulty`, `is_deleted`, `created_at`, `updated_at`)
VALUES ('高等数学', '数学', '大学公共课：极限、导数与积分基础', 3, 0, NOW(), NOW());

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `description`, `tags`, `difficulty`, `weight`, `sort_order`, `is_deleted`, `created_at`, `updated_at`)
VALUES (1, NULL, '极限求解', '数列与函数极限的计算方法', '考点,重点', 3, 1.00, 1, 0, NOW(), NOW()),
       (1, NULL, '导数计算', '基本初等函数求导与复合函数求导', '重点,易错点', 4, 1.20, 2, 0, NOW(), NOW()),
       (1, NULL, '积分应用', '定积分的计算与几何应用', '考点', 3, 1.00, 3, 0, NOW(), NOW());

INSERT INTO `question` (`course_id`, `knowledge_point_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `source`, `is_deleted`, `created_at`, `updated_at`)
VALUES (1, 1, 'SUBJECTIVE', '求极限 lim(x→0) (sin x)/x。', NULL, '1', '第一个重要极限，结果为 1。', 2, '演示题库', 0, NOW(), NOW()),
       (1, 1, 'CHOICE', '下列极限中存在的是（  ）', '["A. lim(x→∞) sin x", "B. lim(x→∞) 1/x", "C. lim(x→∞) x^2", "D. lim(x→∞) cos x"]', 'B', '1/x 当 x→∞ 时趋于 0。', 2, '演示题库', 0, NOW(), NOW()),
       (1, 2, 'SUBJECTIVE', '求函数 f(x)=x^2·sin x 的导数。', NULL, '2x·sin x + x^2·cos x', '乘积法则：(uv)′=u′v+uv′。', 3, '演示题库', 0, NOW(), NOW()),
       (1, 2, 'SUBJECTIVE', '求 f(x)=e^(2x) 的导数。', NULL, '2e^(2x)', '复合函数链式法则。', 3, '演示题库', 0, NOW(), NOW()),
       (1, 3, 'SUBJECTIVE', '计算定积分 ∫₀¹ x dx。', NULL, '1/2', '牛顿-莱布尼茨公式。', 2, '演示题库', 0, NOW(), NOW());
