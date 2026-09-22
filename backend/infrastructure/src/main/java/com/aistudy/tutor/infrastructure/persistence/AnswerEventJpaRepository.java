package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.AnswerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作答事件 JPA 仓库
 */
@Repository
public interface AnswerEventJpaRepository extends JpaRepository<AnswerEvent, Long> {

    boolean existsByRequestId(String requestId);

    List<AnswerEvent> findByUserIdAndKnowledgePointIdOrderByCreatedAtAsc(Long userId, Long kpId);

    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime from);

    long countByUserIdAndCreatedAtAfterAndCorrectTrue(Long userId, LocalDateTime from);
}
