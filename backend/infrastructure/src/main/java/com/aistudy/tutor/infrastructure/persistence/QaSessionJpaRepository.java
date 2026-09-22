package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.qa.model.QaSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 答疑会话 JPA 仓储（Spring Data）
 */
@Repository
public interface QaSessionJpaRepository extends JpaRepository<QaSession, Long> {
    List<QaSession> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);
}
