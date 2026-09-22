package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.LearningPath;
import com.aistudy.tutor.domain.course.repository.LearningPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 学习路径仓储 Port 实现（findUsable 在内存中过滤 user_id 为空或匹配当前用户）
 */
@Component
@RequiredArgsConstructor
public class LearningPathRepositoryImpl implements LearningPathRepository {

    private final LearningPathJpaRepository jpaRepository;

    @Override
    public LearningPath save(LearningPath path) {
        return jpaRepository.save(path);
    }

    @Override
    public Optional<LearningPath> findUsable(Long courseId, Long userId) {
        return jpaRepository.findByCourseIdAndDeletedFalseOrderByCreatedAtDesc(courseId).stream()
                .filter(p -> p.getUserId() == null || p.getUserId().equals(userId))
                .findFirst();
    }
}
