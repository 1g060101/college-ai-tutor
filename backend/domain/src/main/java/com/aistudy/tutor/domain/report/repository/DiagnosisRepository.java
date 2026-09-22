package com.aistudy.tutor.domain.report.repository;

import com.aistudy.tutor.domain.report.model.Diagnosis;

import java.util.Optional;

/**
 * 学习诊断仓库 Port
 */
public interface DiagnosisRepository {

    Diagnosis save(Diagnosis diagnosis);

    /** 指定用户指定周期的最近一条诊断 */
    Optional<Diagnosis> findTopByUserIdAndPeriodOrderByCreatedAtDesc(Long userId, String period);
}
