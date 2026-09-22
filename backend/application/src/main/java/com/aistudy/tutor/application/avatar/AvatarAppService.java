package com.aistudy.tutor.application.avatar;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.avatar.model.AvatarChat;
import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import com.aistudy.tutor.domain.avatar.model.FocusSession;
import com.aistudy.tutor.domain.avatar.repository.AvatarChatRepository;
import com.aistudy.tutor.domain.avatar.repository.AvatarConfigRepository;
import com.aistudy.tutor.domain.avatar.repository.FocusSessionRepository;
import com.aistudy.tutor.infrastructure.aigateway.AiCallFacade;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数字人学习陪伴应用服务：形象配置 / 陪伴对话 / 专注计时。
 */
@Service
@RequiredArgsConstructor
public class AvatarAppService {

    /** 默认数字人配置（无记录时兜底） */
    private static final String DEFAULT_AVATAR_NAME = "小灵";
    private static final String DEFAULT_TONE = "FRIENDLY";
    private static final String DEFAULT_STYLE = "CONCISE";

    /** 语气→人格描述 */
    private static final Map<String, String> TONE_DESC = Map.of(
            "FRIENDLY", "亲切友善",
            "STRICT", "严谨细致",
            "CASUAL", "轻松随性");
    /** 风格→讲解风格描述 */
    private static final Map<String, String> STYLE_DESC = Map.of(
            "CONCISE", "言简意赅",
            "DETAILED", "详尽充分",
            "MOTIVATIONAL", "积极鼓励");

    private final AvatarConfigRepository avatarConfigRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final AvatarChatRepository avatarChatRepository;
    private final AiCallFacade aiCallFacade;

    /**
     * 获取当前用户数字人配置；无记录时返回默认配置（不落库）
     */
    @Transactional(readOnly = true)
    public AvatarConfig getConfig(Long userId) {
        return avatarConfigRepository.findByUserId(userId)
                .orElseGet(() -> new AvatarConfig(userId, DEFAULT_AVATAR_NAME, DEFAULT_TONE, DEFAULT_STYLE, null));
    }

    /**
     * 更新数字人配置：按 user_id 唯一 upsert，空字段保持原值/默认值
     */
    @Transactional
    public AvatarConfig updateConfig(Long userId, String avatarName, String tone, String style, String voice) {
        AvatarConfig config = avatarConfigRepository.findByUserId(userId).orElse(null);
        if (config == null) {
            config = new AvatarConfig(userId,
                    defaultIfBlank(avatarName, DEFAULT_AVATAR_NAME),
                    defaultIfBlank(tone, DEFAULT_TONE),
                    defaultIfBlank(style, DEFAULT_STYLE),
                    voice);
        } else {
            config.update(avatarName, tone, style, voice);
        }
        return avatarConfigRepository.save(config);
    }

    /**
     * 陪伴对话：按「数字人形象配置」构建陪伴人设，直接调用 AI 生成
     * 契合语气/风格并有情感共鸣的回复，记录 USER / AVATAR 两条陪伴记录。返回 AI 回复文本。
     */
    @Transactional
    public String chat(Long userId, Long courseId, String content) {
        AvatarConfig config = getConfig(userId);
        String system = buildCompanionSystem(config);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", content));
        AiCallResult res = aiCallFacade.chat(userId, system, messages, 0.9);
        String reply = (res != null && res.content() != null && !res.content().isBlank())
                ? res.content().trim() : "我在呢，你慢慢说～";
        avatarChatRepository.save(new AvatarChat(userId, "USER", content, null));
        avatarChatRepository.save(new AvatarChat(userId, "AVATAR", reply, null));
        return reply;
    }

    /**
     * 由形象配置构建陪伴人设 system prompt：语气 / 风格 / 音色代入，
     * 并约定情感安抚与口语化表达，避免落入答疑三段式模板。
     */
    private String buildCompanionSystem(AvatarConfig config) {
        String tone = TONE_DESC.getOrDefault(config.getTone(), "亲切友善");
        String style = STYLE_DESC.getOrDefault(config.getStyle(), "自然随和");
        String voice = (config.getVoice() == null || config.getVoice().isBlank())
                ? "" : "\n你的音色：%s".formatted(config.getVoice());
        return "你是数字人学习陪伴「%s」，说话%s、讲解%s。%s"
                .formatted(config.getAvatarName(), tone, style, voice)
                + "\n你的职责是陪伴大学生，要有温度、共情、口语化。"
                + "\n规则：1) 当用户表达情绪（累、焦虑、想放松等）时，先共情安抚，再给轻松实用的小建议；"
                + "2) 当用户问学习/题目时，用鼓励式口语讲清要点，\"严禁\"出现「思路、分级提示、总结（一）（二）（三）」这类答疑三段式；"
                + "3) 回复简短自然、贴合人设，一般不超过150字。";
    }

    /**
     * 创建专注计时：status=RUNNING，start_time=now；durationMinutes 为前台预估
     */
    @Transactional
    public FocusSession createFocusSession(Long userId, Long courseId, Integer durationMinutes) {
        return focusSessionRepository.save(new FocusSession(userId, courseId, LocalDateTime.now(), durationMinutes));
    }

    /**
     * 结束专注：校验归属；计算 duration_minutes（前台 frontendMinutes 优先）；
     * 时长聚合 study_minutes_agg 依赖已有 Port StudyMinutesAggRepository，
     * 但该 Port 仅有 findByUserIdAndStatDateBetween 查询方法、无 save 能力，
     * 为避免改动已存在文件，聚合落库留待阶段 3 完善，此处仅返回本次专注会话。
     */
    @Transactional
    public FocusSession endFocusSession(Long userId, Long id, Integer frontendMinutes) {
        FocusSession session = focusSessionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "专注会话不存在"));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该专注会话");
        }
        if (!"RUNNING".equals(session.getStatus())) {
            throw new BusinessException(400, "专注会话已结束");
        }
        session.finish(LocalDateTime.now(), frontendMinutes);
        return focusSessionRepository.save(session);
    }

    private String defaultIfBlank(String value, String def) {
        return (value == null || value.isBlank()) ? def : value;
    }
}
