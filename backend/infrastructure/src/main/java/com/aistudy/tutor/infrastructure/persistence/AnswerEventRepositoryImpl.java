package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.AnswerEvent;
import com.aistudy.tutor.domain.question.repository.AnswerEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作答事件仓库 Port 实现
 */
@Component
@RequiredArgsConstructor
public class AnswerEventRepositoryImpl implements AnswerEventRepository {

    private final AnswerEventJpaRepository jpaRepository;

    @Override
    public AnswerEvent save(AnswerEvent answerEvent) {
        return jpaRepository.save(answerEvent);
    }

    @Override
    public boolean existsByRequestId(String requestId) {
        return jpaRepository.existsByRequestId(requestId);
    }

    @Override
    public List<AnswerEvent> findByUserIdAndKnowledgePointIdOrderByCreatedAtAsc(Long userId, Long kpId) {
        return jpaRepository.findByUserIdAndKnowledgePointIdOrderByCreatedAtAsc(userId, kpId);
    }

    @Override
    public long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime from) {
        return jpaRepository.countByUserIdAndCreatedAtAfter(userId, from);
    }

    @Override
    public long countByUserIdAndCreatedAtAfterAndCorrectTrue(Long userId, LocalDateTime from) {
        return jpaRepository.countByUserIdAndCreatedAtAfterAndCorrectTrue(userId, from);
    }
}
