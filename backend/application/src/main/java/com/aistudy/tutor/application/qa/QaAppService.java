package com.aistudy.tutor.application.qa;

import com.aistudy.tutor.application.report.MasteryRecalcService;
import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.qa.model.QaSession;
import com.aistudy.tutor.domain.qa.model.QaSessionStatus;
import com.aistudy.tutor.domain.qa.model.QaTurn;
import com.aistudy.tutor.domain.qa.model.ReplyMode;
import com.aistudy.tutor.domain.qa.repository.QaSessionRepository;
import com.aistudy.tutor.domain.qa.repository.QaTurnRepository;
import com.aistudy.tutor.domain.qa.service.GuidancePolicy;
import com.aistudy.tutor.infrastructure.aigateway.AiCallFacade;
import com.aistudy.tutor.shared.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI 智能答疑应用服务：会话管理 / 流式提问（SSE）/ 拍照提问 / 反馈回写掌握度
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QaAppService {

    /** 作为上下文携带给 LLM 的历史轮数 */
    private static final int HISTORY_TURNS = 6;

    private final QaSessionRepository qaSessionRepository;
    private final QaTurnRepository qaTurnRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final AiCallFacade aiCallFacade;
    private final MasteryRecalcService masteryRecalcService;
    private final ObjectMapper objectMapper;

    /** 引导式教学策略：纯领域服务（无 Spring 注解），直接持有实例 */
    private static final GuidancePolicy GUIDANCE_POLICY = new GuidancePolicy();

    /**
     * 新建答疑会话
     */
    @Transactional
    public QaSession createSession(Long userId, Long courseId, String title, ReplyMode replyMode) {
        QaSession session = new QaSession(userId, courseId, title, replyMode == null ? ReplyMode.GUIDED : replyMode);
        return qaSessionRepository.save(session);
    }

    /**
     * 校验会话存在且归属当前用户（供 Controller 在 SSE 建立前前置校验）
     */
    @Transactional(readOnly = true)
    public QaSession validateSessionAccess(Long userId, Long sessionId) {
        QaSession session = qaSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "答疑会话不存在"));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该答疑会话");
        }
        return session;
    }

    @Transactional(readOnly = true)
    public QaSession getSession(Long userId, Long sessionId) {
        return validateSessionAccess(userId, sessionId);
    }

    /**
     * 查询会话全部对话轮次
     */
    @Transactional(readOnly = true)
    public List<QaTurn> listTurns(Long userId, Long sessionId) {
        validateSessionAccess(userId, sessionId);
        return qaTurnRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    /**
     * 组装 LLM 消息：取最近 6 轮历史（user/assistant 交替），最后追加当前问题
     */
    @Transactional(readOnly = true)
    protected List<Map<String, String>> buildMessages(Long sessionId, String question) {
        List<QaTurn> turns = qaTurnRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        List<Map<String, String>> messages = new ArrayList<>();
        int from = Math.max(0, turns.size() - HISTORY_TURNS);
        for (int i = from; i < turns.size(); i++) {
            QaTurn turn = turns.get(i);
            messages.add(Map.of("role", "user", "content", turn.getQuestion()));
            messages.add(Map.of("role", "assistant", "content", turn.getAnswer()));
        }
        messages.add(Map.of("role", "user", "content", question));
        return messages;
    }

    /**
     * 匹配命中的知识点 id：取 question 中包含的最长知识点名称（中文子串匹配），无则 null
     */
    private Long matchKnowledgePoint(String question) {
        if (question == null || question.isBlank()) {
            return null;
        }
        KnowledgePoint best = null;
        for (KnowledgePoint kp : knowledgePointRepository.findAllActive()) {
            String name = kp.getName();
            if (name != null && !name.isBlank() && question.contains(name)) {
                if (best == null || name.length() > best.getName().length()) {
                    best = kp;
                }
            }
        }
        return best == null ? null : best.getId();
    }

    /**
     * 记录用户反馈：写回轮次反馈文本，命中知识点则回写掌握度事件
     */
    @Transactional
    public QaTurn feedback(Long userId, Long sessionId, Long turnId, String feedbackText, boolean helpful, boolean correct) {
        QaSession session = validateSessionAccess(userId, sessionId);
        QaTurn turn = qaTurnRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .filter(t -> t.getId().equals(turnId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "该轮对话不存在"));
        turn.markFeedback(feedbackText);
        // helpful 预留给后续满意度统计；只有命中知识点才回写掌握度
        if (turn.getKpHit() != null) {
            masteryRecalcService.recordEvent(userId, session.getCourseId(), turn.getKpHit(),
                    correct, null, "QA", UUID.randomUUID().toString());
        }
        return turn;
    }

    /**
     * 流式答疑（SSE）。不声明事务，避免长连接占用事务资源。
     * 会话不存在/归属错误/已关闭会在 SSE 建立前抛出，交由全局异常处理器返回 JSON。
     */
    public void askStream(Long userId, Long sessionId, String question, SseEmitter emitter) {
        QaSession session = validateSessionAccess(userId, sessionId);
        if (session.getStatus() != QaSessionStatus.ACTIVE) {
            throw new BusinessException(400, "答疑会话已关闭");
        }
        Long kpHit = matchKnowledgePoint(question);
        String replyModeName = session.getReplyMode() == ReplyMode.DIRECT ? "DIRECT" : "GUIDED";
        String system = session.getReplyMode() == ReplyMode.DIRECT
                ? GUIDANCE_POLICY.directSystemPrompt() : GUIDANCE_POLICY.guidedSystemPrompt();
        List<Map<String, String>> messages = buildMessages(sessionId, question);
        StringBuilder buffer = new StringBuilder();
        try {
            aiCallFacade.chatStream(userId, system, messages, 0.7,
                    // onChunk：累积片段并推送给前端
                    chunk -> {
                        buffer.append(chunk);
                        try {
                            emitter.send(SseEmitter.event().name("chunk").data(chunk));
                        } catch (IOException e) {
                            log.warn("SSE 推送 chunk 失败: {}", e.getMessage());
                        }
                    },
                    // onDone：拿到完整回复后做引导式校验、保存轮次、推送 done 事件
                    result -> {
                        try {
                            String content = result.content();
                            String answer = content;
                            if (session.getReplyMode() == ReplyMode.GUIDED) {
                                answer = applyGuidedGuard(userId, content, emitter);
                            }
                            String guidanceLevel = extractGuidanceLevel(answer);
                            QaTurn turn = saveTurn(userId, sessionId, question, answer, kpHit, guidanceLevel, replyModeName);
                            Map<String, Object> doneData = new LinkedHashMap<>();
                            doneData.put("turnId", turn.getId());
                            doneData.put("kpHit", kpHit);
                            doneData.put("guidanceLevel", guidanceLevel);
                            doneData.put("replyMode", replyModeName);
                            emitter.send(SseEmitter.event().name("done").data(objectMapper.writeValueAsString(doneData)));
                            emitter.complete();
                        } catch (Exception e) {
                            log.error("答疑流式处理异常 sessionId={}", sessionId, e);
                            sendError(emitter, e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("答疑流式调用异常 sessionId={}", sessionId, e);
            sendError(emitter, e.getMessage());
        }
    }

    /**
     * 同步答疑（拍照提问复用）：一次拿全量回复，同样做引导式校验与保存
     */
    @Transactional
    public QaTurn askSync(Long userId, Long sessionId, String question) {
        QaSession session = validateSessionAccess(userId, sessionId);
        if (session.getStatus() != QaSessionStatus.ACTIVE) {
            throw new BusinessException(400, "答疑会话已关闭");
        }
        Long kpHit = matchKnowledgePoint(question);
        String replyModeName = session.getReplyMode() == ReplyMode.DIRECT ? "DIRECT" : "GUIDED";
        String system = session.getReplyMode() == ReplyMode.DIRECT
                ? GUIDANCE_POLICY.directSystemPrompt() : GUIDANCE_POLICY.guidedSystemPrompt();
        List<Map<String, String>> messages = buildMessages(sessionId, question);
        AiCallResult result = aiCallFacade.chat(userId, system, messages, 0.7);
        String answer = applyGuidedGuard(userId, result.content(), null);
        String guidanceLevel = extractGuidanceLevel(answer);
        return saveTurn(userId, sessionId, question, answer, kpHit, guidanceLevel, replyModeName);
    }

    /**
     * 引导式守门：启发式命中"直接给答案"或结构不完整时，走 LLM 二次校验 + 重写。
     * 重写成功则向 SSE 推送 guided_corrected 事件；任一步失败降级使用原回复。
     */
    private String applyGuidedGuard(Long userId, String content, SseEmitter emitter) {
        if (content == null || content.isBlank()) {
            return content;
        }
        if (GUIDANCE_POLICY.isDirectAnswer(content) || !GUIDANCE_POLICY.isGuidedStructure(content)) {
            try {
                AiCallResult check = aiCallFacade.chat(userId, "",
                        List.of(Map.of("role", "user", "content", GUIDANCE_POLICY.checkerPrompt(content))), 0);
                if (check.content() != null && check.content().toUpperCase().contains("YES")) {
                    AiCallResult rewritten = aiCallFacade.chat(userId, "",
                            List.of(Map.of("role", "user", "content", GUIDANCE_POLICY.rewritePrompt(content))), 0);
                    if (rewritten.content() != null && !rewritten.content().isBlank()) {
                        if (emitter != null) {
                            emitter.send(SseEmitter.event().name("guided_corrected").data(rewritten.content()));
                        }
                        return rewritten.content();
                    }
                }
            } catch (Exception e) {
                // 二次校验/重写失败时降级使用原回复
                log.warn("引导式二次校验失败，降级使用原回复: {}", e.getMessage());
            }
        }
        return content;
    }

    /**
     * 从回复中提取出现的最高级提示（HINT3 > HINT2 > HINT1），无则 null
     */
    private String extractGuidanceLevel(String answer) {
        if (answer == null) {
            return null;
        }
        if (answer.contains("HINT3")) {
            return "HINT3";
        }
        if (answer.contains("HINT2")) {
            return "HINT2";
        }
        if (answer.contains("HINT1")) {
            return "HINT1";
        }
        return null;
    }

    /**
     * 保存单轮对话（单条插入，无需外层事务）
     */
    private QaTurn saveTurn(Long userId, Long sessionId, String question, String answer,
                            Long kpHit, String guidanceLevel, String replyModeName) {
        return qaTurnRepository.save(new QaTurn(sessionId, userId, question, answer, kpHit, guidanceLevel, null, replyModeName));
    }

    /**
     * 推送 error 事件并结束 SSE 连接
     */
    private void sendError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("error").data(message == null ? "AI 服务异常" : message));
        } catch (IOException e) {
            log.warn("SSE 发送 error 事件失败: {}", e.getMessage());
        }
        emitter.complete();
    }
}
