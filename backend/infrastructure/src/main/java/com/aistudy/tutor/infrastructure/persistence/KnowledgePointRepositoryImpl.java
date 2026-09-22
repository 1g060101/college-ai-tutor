package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 知识点仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class KnowledgePointRepositoryImpl implements KnowledgePointRepository {

    private final KnowledgePointJpaRepository jpaRepository;

    @Override
    public List<KnowledgePoint> findAllActive() {
        return jpaRepository.findByDeletedFalse();
    }

    @Override
    public Optional<KnowledgePoint> findById(Long id) {
        return jpaRepository.findById(id).filter(kp -> !kp.isDeleted());
    }

    @Override
    public KnowledgePoint save(KnowledgePoint knowledgePoint) {
        return jpaRepository.save(knowledgePoint);
    }
}
