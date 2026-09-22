package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.AvatarChat;
import com.aistudy.tutor.domain.avatar.repository.AvatarChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 数字人陪伴对话记录仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class AvatarChatRepositoryImpl implements AvatarChatRepository {

    private final AvatarChatJpaRepository jpaRepository;

    @Override
    public AvatarChat save(AvatarChat chat) {
        return jpaRepository.save(chat);
    }
}
