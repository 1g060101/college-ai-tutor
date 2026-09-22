package com.aistudy.tutor.domain.qa.repository;

import com.aistudy.tutor.domain.qa.model.QaSession;

import java.util.List;
import java.util.Optional;

/**
 * 答疑会话仓储 Port（实现在 infrastructure.persistence）
 */
public interface QaSessionRepository {

    QaSession save(QaSession session);

    Optional<QaSession> findById(Long id);

    List<QaSession> findByUserId(Long userId);
}
