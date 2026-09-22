package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 学习记录 JPA 仓储（Spring Data）
 */
@Repository
public interface StudyRecordJpaRepository extends JpaRepository<StudyRecord, Long> {
}
