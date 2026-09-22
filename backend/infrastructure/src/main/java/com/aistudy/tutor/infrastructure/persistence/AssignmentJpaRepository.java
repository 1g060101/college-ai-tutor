package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 作业 JPA 仓储（Spring Data）
 */
@Repository
public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {
}
