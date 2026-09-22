package com.aistudy.tutor.application.report;

import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.question.model.AnswerEvent;
import com.aistudy.tutor.domain.question.repository.AnswerEventRepository;
import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.model.UserKnowledgePointId;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import com.aistudy.tutor.domain.report.service.MasteryEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 掌握度重算应用服务：记录作答事件并同步增量重算受影响知识点的掌握度。
 * 注：文档要求异步增量重算，本项目为演示确定性采用同步增量重算（仅重算受影响的 kp）。
 */
@Service
@RequiredArgsConstructor
public class MasteryRecalcService {

    private final AnswerEventRepository answerEventRepository;
    private final UserKnowledgePointRepository userKnowledgePointRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final MasteryEngine masteryEngine;

    /**
     * 记录答疑 / 学习类事件（无题目上下文，questionId 用哨兵 0）。
     */
    @Transactional
    public void recordEvent(Long userId, Long courseId, Long knowledgePointId, boolean correct,
                            BigDecimal score, String source, String requestId) {
        recordEventWithQuestion(userId, 0L, courseId, knowledgePointId, correct, score, source, requestId);
    }

    /**
     * 记录题目作答事件：幂等（requestId 唯一），落库后触发该知识点掌握度重算。
     * score 为 null 时正确记 100、错误记 0。
     */
    @Transactional
    public void recordEventWithQuestion(Long userId, Long questionId, Long courseId, Long knowledgePointId,
                                        boolean correct, BigDecimal score, String source, String requestId) {
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (answerEventRepository.existsByRequestId(requestId)) {
            return;
        }
        BigDecimal finalScore = score != null ? score : (correct ? BigDecimal.valueOf(100) : BigDecimal.ZERO);
        AnswerEvent event = new AnswerEvent(userId, questionId, courseId, knowledgePointId,
                null, correct, finalScore, null, source, requestId);
        answerEventRepository.save(event);
        // 无知识点归属的事件仅记录，无法参与掌握度聚合
        if (knowledgePointId != null) {
            recalc(userId, knowledgePointId);
        }
    }

    /**
     * 重算指定用户指定知识点的掌握度：EWMA 聚合全部作答事件，upsert user_knowledge_point。
     */
    @Transactional
    public void recalc(Long userId, Long knowledgePointId) {
        if (knowledgePointId == null) {
            return;
        }
        List<AnswerEvent> events = answerEventRepository
                .findByUserIdAndKnowledgePointIdOrderByCreatedAtAsc(userId, knowledgePointId);
        // difficulty 取知识点难度，取不到默认 3
        int difficulty = knowledgePointRepository.findById(knowledgePointId)
                .map(KnowledgePoint::getDifficulty)
                .orElse(3);
        List<MasteryEngine.EventInput> inputs = events.stream()
                .map(e -> new MasteryEngine.EventInput(e.isCorrect(), e.getScore(), difficulty, e.getSource()))
                .toList();
        double mastery = masteryEngine.aggregate(inputs);
        long correctCount = events.stream().filter(AnswerEvent::isCorrect).count();
        LocalDateTime lastAnsweredAt = events.isEmpty() ? null : events.get(events.size() - 1).getCreatedAt();

        UserKnowledgePointId id = new UserKnowledgePointId(userId, knowledgePointId);
        UserKnowledgePoint ukp = userKnowledgePointRepository.findById(id).orElseGet(() -> new UserKnowledgePoint(id));
        ukp.updateMastery(BigDecimal.valueOf(mastery), events.size(), (int) correctCount, lastAnsweredAt);
        ukp.setRecalcTs(LocalDateTime.now());
        userKnowledgePointRepository.save(ukp);
    }
}
