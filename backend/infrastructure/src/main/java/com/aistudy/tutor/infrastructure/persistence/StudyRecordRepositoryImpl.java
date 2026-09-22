package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.StudyRecord;
import com.aistudy.tutor.domain.studyplan.repository.StudyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 学习记录仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class StudyRecordRepositoryImpl implements StudyRecordRepository {

    private final StudyRecordJpaRepository jpaRepository;

    @Override
    public StudyRecord save(StudyRecord record) {
        return jpaRepository.save(record);
    }
}
