package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.AlertLog;
import com.aistudy.tutor.domain.classroom.repository.AlertLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 预警日志仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class AlertLogRepositoryImpl implements AlertLogRepository {

    private final AlertLogJpaRepository jpaRepository;

    @Override
    public AlertLog save(AlertLog alertLog) {
        return jpaRepository.save(alertLog);
    }

    @Override
    public boolean existsByTeacherIdAndClassIdAndStudentIdAndCreatedAtBetween(Long teacherId, Long classId,
                                                                              Long studentId, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.existsByTeacherIdAndClassIdAndStudentIdAndCreatedAtBetween(teacherId, classId, studentId, start, end);
    }

    @Override
    public long countByTeacherIdAndClassIdAndStudentId(Long teacherId, Long classId, Long studentId) {
        return jpaRepository.countByTeacherIdAndClassIdAndStudentId(teacherId, classId, studentId);
    }
}
