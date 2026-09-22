package com.aistudy.tutor.domain.question.repository;

import com.aistudy.tutor.domain.question.model.AnswerEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作答事件仓库 Port
 */
public interface AnswerEventRepository {

    AnswerEvent save(AnswerEvent answerEvent);

    /** 幂等校验：requestId 是否已存在 */
    boolean existsByRequestId(String requestId);

    /** 指定用户指定知识点的全部作答事件，按时间升序（掌握度重算输入） */
    List<AnswerEvent> findByUserIdAndKnowledgePointIdOrderByCreatedAtAsc(Long userId, Long kpId);

    /** 指定时间之后的作答总数 */
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime from);

    /** 指定时间之后的答对总数 */
    long countByUserIdAndCreatedAtAfterAndCorrectTrue(Long userId, LocalDateTime from);
}
