package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Summary;
import com.aistudy.tutor.domain.classroom.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 要点提炼仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class SummaryRepositoryImpl implements SummaryRepository {

    private final SummaryJpaRepository jpaRepository;

    @Override
    public Summary save(Summary summary) {
        return jpaRepository.save(summary);
    }
}
