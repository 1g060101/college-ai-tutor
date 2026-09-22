package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.AvatarChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 数字人陪伴对话记录 JPA 仓储（Spring Data）
 */
@Repository
public interface AvatarChatJpaRepository extends JpaRepository<AvatarChat, Long> {
}
