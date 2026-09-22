package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.Diagnosis;
import com.aistudy.tutor.domain.report.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 学习诊断仓库 Port 实现
 */
@Component
@RequiredArgsConstructor
public class DiagnosisRepositoryImpl implements DiagnosisRepository {

    private final DiagnosisJpaRepository jpaRepository;

    @Override
    public Diagnosis save(Diagnosis diagnosis) {
        return jpaRepository.save(diagnosis);
    }

    @Override
    public Optional<Diagnosis> findTopByUserIdAndPeriodOrderByCreatedAtDesc(Long userId, String period) {
        return jpaRepository.findTopByUserIdAndPeriodOrderByCreatedAtDesc(userId, period);
    }
}
