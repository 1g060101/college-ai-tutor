package com.aistudy.tutor.domain.avatar.repository;

import com.aistudy.tutor.domain.avatar.model.AvatarChat;

/**
 * 数字人陪伴对话记录仓储 Port（实现在 infrastructure.persistence）
 */
public interface AvatarChatRepository {

    AvatarChat save(AvatarChat chat);
}
