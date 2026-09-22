package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 预习复习计划 JPA 仓储（Spring Data）
 */
@Repository
public interface StudyPlanJpaRepository extends JpaRepository<StudyPlan, Long> {
}
