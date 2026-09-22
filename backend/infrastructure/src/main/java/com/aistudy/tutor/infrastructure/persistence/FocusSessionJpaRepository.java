package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 专注计时会话 JPA 仓储（Spring Data）
 */
@Repository
public interface FocusSessionJpaRepository extends JpaRepository<FocusSession, Long> {
}
