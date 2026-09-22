package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学习路径 JPA 仓储（Spring Data）
 */
@Repository
public interface LearningPathJpaRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByCourseIdAndDeletedFalseOrderByCreatedAtDesc(Long courseId);
}
