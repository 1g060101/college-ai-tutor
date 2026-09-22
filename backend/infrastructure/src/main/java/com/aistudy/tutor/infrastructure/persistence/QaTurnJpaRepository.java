package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.qa.model.QaTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 答疑单轮对话 JPA 仓储（Spring Data）
 */
@Repository
public interface QaTurnJpaRepository extends JpaRepository<QaTurn, Long> {
    List<QaTurn> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
