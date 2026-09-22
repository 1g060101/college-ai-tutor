package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.qa.model.QaTurn;
import com.aistudy.tutor.domain.qa.repository.QaTurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 答疑单轮对话仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class QaTurnRepositoryImpl implements QaTurnRepository {

    private final QaTurnJpaRepository jpaRepository;

    @Override
    public QaTurn save(QaTurn turn) {
        return jpaRepository.save(turn);
    }

    @Override
    public List<QaTurn> findBySessionIdOrderByCreatedAtAsc(Long sessionId) {
        return jpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}
