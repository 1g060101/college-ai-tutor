package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.Assignment;

import java.util.Optional;

/**
 * 作业仓储 Port（实现在 infrastructure.persistence）
 */
public interface AssignmentRepository {

    Assignment save(Assignment assignment);

    Optional<Assignment> findById(Long id);
}
