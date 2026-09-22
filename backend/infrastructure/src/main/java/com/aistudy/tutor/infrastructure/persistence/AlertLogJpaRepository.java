package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 预警日志 JPA 仓储（Spring Data）
 */
@Repository
public interface AlertLogJpaRepository extends JpaRepository<AlertLog, Long> {

    boolean existsByTeacherIdAndClassIdAndStudentIdAndCreatedAtBetween(Long teacherId, Long classId,
                                                                       Long studentId, LocalDateTime start, LocalDateTime end);

    long countByTeacherIdAndClassIdAndStudentId(Long teacherId, Long classId, Long studentId);
}
