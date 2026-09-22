package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 学习诊断 JPA 仓库
 */
@Repository
public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findTopByUserIdAndPeriodOrderByCreatedAtDesc(Long userId, String period);
}
