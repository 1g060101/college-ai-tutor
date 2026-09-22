package com.aistudy.tutor.domain.studyplan.repository;

import com.aistudy.tutor.domain.studyplan.model.StudyPlan;

import java.util.Optional;

/**
 * 预习复习计划仓储 Port（实现在 infrastructure.persistence）
 */
public interface StudyPlanRepository {

    StudyPlan save(StudyPlan plan);

    Optional<StudyPlan> findById(Long id);
}
