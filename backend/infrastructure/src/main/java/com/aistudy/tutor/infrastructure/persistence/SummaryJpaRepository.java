package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 要点提炼 JPA 仓储（Spring Data）
 */
@Repository
public interface SummaryJpaRepository extends JpaRepository<Summary, Long> {
}
