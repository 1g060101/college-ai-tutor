package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.avatar.model.FocusSession;
import com.aistudy.tutor.domain.avatar.repository.FocusSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 专注计时会话仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class FocusSessionRepositoryImpl implements FocusSessionRepository {

    private final FocusSessionJpaRepository jpaRepository;

    @Override
    public FocusSession save(FocusSession session) {
        return jpaRepository.save(session);
    }

    @Override
    public Optional<FocusSession> findById(Long id) {
        return jpaRepository.findById(id).filter(s -> !s.isDeleted());
    }
}
