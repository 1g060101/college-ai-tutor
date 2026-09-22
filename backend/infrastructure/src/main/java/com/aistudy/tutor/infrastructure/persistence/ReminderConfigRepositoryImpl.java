package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.ReminderConfig;
import com.aistudy.tutor.domain.question.repository.ReminderConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 提醒配置仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class ReminderConfigRepositoryImpl implements ReminderConfigRepository {

    private final ReminderConfigJpaRepository jpaRepository;

    @Override
    public ReminderConfig save(ReminderConfig config) {
        return jpaRepository.save(config);
    }

    @Override
    public Optional<ReminderConfig> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }
}
