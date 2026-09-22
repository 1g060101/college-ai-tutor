package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.StudyMinutesAgg;
import com.aistudy.tutor.domain.report.repository.StudyMinutesAggRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习时长聚合仓库 Port 实现
 */
@Component
@RequiredArgsConstructor
public class StudyMinutesAggRepositoryImpl implements StudyMinutesAggRepository {

    private final StudyMinutesAggJpaRepository jpaRepository;

    @Override
    public List<StudyMinutesAgg> findByUserIdAndStatDateBetween(Long userId, LocalDate from, LocalDate to) {
        return jpaRepository.findByUserIdAndStatDateBetween(userId, from, to);
    }
}
