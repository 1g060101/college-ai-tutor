package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import com.aistudy.tutor.domain.avatar.repository.AvatarConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 数字人形象配置仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class AvatarConfigRepositoryImpl implements AvatarConfigRepository {

    private final AvatarConfigJpaRepository jpaRepository;

    @Override
    public AvatarConfig save(AvatarConfig config) {
        return jpaRepository.save(config);
    }

    @Override
    public Optional<AvatarConfig> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalse(userId);
    }
}
