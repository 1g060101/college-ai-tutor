package com.aistudy.tutor.domain.course.repository;

import com.aistudy.tutor.domain.course.model.LearningPath;

import java.util.Optional;

/**
 * 学习路径仓储 Port（实现在 infrastructure.persistence）
 */
public interface LearningPathRepository {

    LearningPath save(LearningPath path);

    /**
     * 查找课程下当前用户可用路径：user_id 匹配指定用户或为空（通用路径）均可，
     * 多条时取最新创建的一条。
     */
    Optional<LearningPath> findUsable(Long courseId, Long userId);
}
