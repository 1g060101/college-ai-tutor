package com.aistudy.tutor.domain.course.repository;

import com.aistudy.tutor.domain.course.model.KnowledgePoint;

import java.util.List;
import java.util.Optional;

/**
 * 知识点仓储 Port（实现在 infrastructure.persistence）
 */
public interface KnowledgePointRepository {

    List<KnowledgePoint> findAllActive();

    Optional<KnowledgePoint> findById(Long id);
}
