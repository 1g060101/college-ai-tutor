package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.AlertLog;

import java.time.LocalDateTime;

/**
 * 预警日志仓储 Port（实现在 infrastructure.persistence）
 */
public interface AlertLogRepository {

    AlertLog save(AlertLog alertLog);

    /** 指定学生当日（[start, end)）是否已生成过预警，用于防误报去重 */
    boolean existsByTeacherIdAndClassIdAndStudentIdAndCreatedAtBetween(Long teacherId, Long classId,
                                                                       Long studentId, LocalDateTime start, LocalDateTime end);

    long countByTeacherIdAndClassIdAndStudentId(Long teacherId, Long classId, Long studentId);
}
