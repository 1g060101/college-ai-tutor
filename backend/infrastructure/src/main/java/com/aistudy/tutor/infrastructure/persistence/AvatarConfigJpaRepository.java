package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 数字人形象配置 JPA 仓储（Spring Data）
 */
@Repository
public interface AvatarConfigJpaRepository extends JpaRepository<AvatarConfig, Long> {
    Optional<AvatarConfig> findByUserIdAndDeletedFalse(Long userId);
}
