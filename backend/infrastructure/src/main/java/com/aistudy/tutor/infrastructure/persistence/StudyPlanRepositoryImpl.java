package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.StudyPlan;
import com.aistudy.tutor.domain.studyplan.repository.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 预习复习计划仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class StudyPlanRepositoryImpl implements StudyPlanRepository {

    private final StudyPlanJpaRepository jpaRepository;

    @Override
    public StudyPlan save(StudyPlan plan) {
        return jpaRepository.save(plan);
    }

    @Override
    public Optional<StudyPlan> findById(Long id) {
        return jpaRepository.findById(id).filter(p -> !p.isDeleted());
    }
}
