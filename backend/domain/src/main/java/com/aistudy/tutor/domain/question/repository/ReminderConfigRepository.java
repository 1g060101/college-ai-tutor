package com.aistudy.tutor.domain.question.repository;

import com.aistudy.tutor.domain.question.model.ReminderConfig;

import java.util.Optional;

/**
 * 提醒配置仓储 Port（实现在 infrastructure.persistence）
 */
public interface ReminderConfigRepository {

    ReminderConfig save(ReminderConfig config);

    Optional<ReminderConfig> findByUserId(Long userId);
}
