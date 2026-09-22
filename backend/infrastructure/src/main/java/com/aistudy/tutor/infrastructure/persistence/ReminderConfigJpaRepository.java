package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.ReminderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 提醒配置 JPA 仓储（Spring Data）
 */
@Repository
public interface ReminderConfigJpaRepository extends JpaRepository<ReminderConfig, Long> {

    Optional<ReminderConfig> findByUserId(Long userId);
}
