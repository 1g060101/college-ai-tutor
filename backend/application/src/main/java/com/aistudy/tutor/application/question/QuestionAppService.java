package com.aistudy.tutor.application.question;

import com.aistudy.tutor.application.report.MasteryRecalcService;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.question.model.Question;
import com.aistudy.tutor.domain.question.repository.QuestionRepository;
import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 题目应用服务：提交作答 / 同类题推荐。
 */
@Service
@RequiredArgsConstructor
public class QuestionAppService {

    private final QuestionRepository questionRepository;
    private final MasteryRecalcService masteryRecalcService;
    private final UserKnowledgePointRepository userKnowledgePointRepository;
    private final KnowledgePointRepository knowledgePointRepository;

    /**
     * 提交题目作答：幂等记录作答事件并触发掌握度重算。
     */
    @Transactional
    public void submitAnswer(Long userId, Long questionId, String answerContent, boolean correct,
                             BigDecimal score, Integer durationSeconds, String requestId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(404, "题目不存在"));
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        masteryRecalcService.recordEventWithQuestion(userId, questionId, question.getCourseId(),
                question.getKnowledgePointId(), correct, score, "HOMEWORK", requestId);
    }

    /**
     * 同类题推荐：同知识点优先（无则同课程），按难度接近 + 弱项优先排序，最多 5 条。
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> similarQuestions(Long userId, Long questionId) {
        Question current = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(404, "题目不存在"));

        // 同知识点其他题；知识点为空则取同课程其他题
        List<Question> candidates = current.getKnowledgePointId() != null
                ? questionRepository.findByKnowledgePointIdAndIdNot(current.getKnowledgePointId(), questionId)
                : questionRepository.findByCourseIdAndDeletedFalseAndIdNot(current.getCourseId(), questionId);

        // 掌握度映射：knowledgePointId -> masteryScore
        Map<Long, BigDecimal> masteryMap = new LinkedHashMap<>();
        for (UserKnowledgePoint ukp : userKnowledgePointRepository.findByUserId(userId)) {
            masteryMap.put(ukp.getId().getKnowledgePointId(), ukp.getMasteryScore());
        }

        List<Map<String, Object>> ranked = new ArrayList<>();
        for (Question q : candidates) {
            BigDecimal mastery = masteryMap.get(q.getKnowledgePointId());
            boolean weak = mastery != null && mastery.compareTo(BigDecimal.valueOf(60)) <= 0;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionId", q.getId());
            item.put("content", q.getContent());
            item.put("type", q.getType());
            item.put("difficulty", q.getDifficulty());
            item.put("masteryScore", mastery);
            item.put("reasoning", buildReasoning(current, q, mastery));
            // 排序辅助键（不对外输出）
            item.put("_weak", weak);
            item.put("_diff", Math.abs(q.getDifficulty() - current.getDifficulty()));
            ranked.add(item);
        }

        // 弱项优先，其次与当前题难度差值小者优先
        ranked.sort((a, b) -> {
            int c = Boolean.compare((Boolean) b.get("_weak"), (Boolean) a.get("_weak"));
            if (c != 0) return c;
            return Integer.compare((Integer) a.get("_diff"), (Integer) b.get("_diff"));
        });

        return ranked.stream().limit(5).map(m -> {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("questionId", m.get("questionId"));
            out.put("content", m.get("content"));
            out.put("type", m.get("type"));
            out.put("difficulty", m.get("difficulty"));
            out.put("masteryScore", m.get("masteryScore"));
            out.put("reasoning", m.get("reasoning"));
            return out;
        }).toList();
    }

    private String buildReasoning(Question current, Question q, BigDecimal mastery) {
        String kpName = current.getKnowledgePointId() != null
                ? knowledgePointRepository.findById(current.getKnowledgePointId()).map(KnowledgePoint::getName).orElse(null)
                : null;
        String namePart = kpName != null ? "『" + kpName + "』" : "";
        String masteryPart = mastery != null ? mastery.toPlainString() : "尚未测评";
        return "该题与当前题同属知识点" + namePart + "，难度接近；你对该知识点掌握度 " + masteryPart + "，建议优先练习。";
    }
}
