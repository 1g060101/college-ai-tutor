package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.qa.model.QaSession;
import com.aistudy.tutor.domain.qa.repository.QaSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 答疑会话仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class QaSessionRepositoryImpl implements QaSessionRepository {

    private final QaSessionJpaRepository jpaRepository;

    @Override
    public QaSession save(QaSession session) {
        return jpaRepository.save(session);
    }

    @Override
    public Optional<QaSession> findById(Long id) {
        return jpaRepository.findById(id).filter(s -> !s.isDeleted());
    }

    @Override
    public List<QaSession> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId);
    }
}
