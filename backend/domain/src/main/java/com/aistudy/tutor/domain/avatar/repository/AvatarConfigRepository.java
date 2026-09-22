package com.aistudy.tutor.domain.avatar.repository;

import com.aistudy.tutor.domain.avatar.model.AvatarConfig;

import java.util.Optional;

/**
 * 数字人形象配置仓储 Port（实现在 infrastructure.persistence）
 */
public interface AvatarConfigRepository {

    AvatarConfig save(AvatarConfig config);

    Optional<AvatarConfig> findByUserId(Long userId);
}
