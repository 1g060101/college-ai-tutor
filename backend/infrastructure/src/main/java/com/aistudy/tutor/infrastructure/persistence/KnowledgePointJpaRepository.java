package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识点 JPA 仓储（Spring Data）
 */
@Repository
public interface KnowledgePointJpaRepository extends JpaRepository<KnowledgePoint, Long> {
    List<KnowledgePoint> findByDeletedFalse();
}
