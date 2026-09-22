package com.aistudy.tutor.domain.avatar.repository;

import com.aistudy.tutor.domain.avatar.model.FocusSession;

import java.util.Optional;

/**
 * 专注计时会话仓储 Port（实现在 infrastructure.persistence）
 */
public interface FocusSessionRepository {

    FocusSession save(FocusSession session);

    Optional<FocusSession> findById(Long id);
}
