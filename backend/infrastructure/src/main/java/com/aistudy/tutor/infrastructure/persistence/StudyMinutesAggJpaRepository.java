package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.StudyMinutesAgg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习时长聚合 JPA 仓库
 */
@Repository
public interface StudyMinutesAggJpaRepository extends JpaRepository<StudyMinutesAgg, Long> {

    List<StudyMinutesAgg> findByUserIdAndStatDateBetween(Long userId, LocalDate from, LocalDate to);
}
