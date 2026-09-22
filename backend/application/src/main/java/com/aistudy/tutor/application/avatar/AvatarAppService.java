package com.aistudy.tutor.application.avatar;

import com.aistudy.tutor.application.qa.QaAppService;
import com.aistudy.tutor.domain.avatar.model.AvatarChat;
import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import com.aistudy.tutor.domain.avatar.model.FocusSession;
import com.aistudy.tutor.domain.avatar.repository.AvatarChatRepository;
import com.aistudy.tutor.domain.avatar.repository.AvatarConfigRepository;
import com.aistudy.tutor.domain.avatar.repository.FocusSessionRepository;
import com.aistudy.tutor.domain.qa.model.QaSession;
import com.aistudy.tutor.domain.qa.model.QaTurn;
import com.aistudy.tutor.domain.qa.model.ReplyMode;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    private final AvatarConfigRepository avatarConfigRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final AvatarChatRepository avatarChatRepository;
    private final QaAppService qaAppService;

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
     * 陪伴对话：复用答疑会话（标题取内容前 20 字、引导式），同步答疑后
     * 把 USER / AVATAR 两条记录写入 avatar_chat，返回答疑轮次
     */
    @Transactional
    public QaTurn chat(Long userId, Long courseId, String content) {
        String title = content.length() > 20 ? content.substring(0, 20) : content;
        QaSession session = qaAppService.createSession(userId, courseId, title, ReplyMode.GUIDED);
        QaTurn turn = qaAppService.askSync(userId, session.getId(), content);
        avatarChatRepository.save(new AvatarChat(userId, "USER", content, null));
        avatarChatRepository.save(new AvatarChat(userId, "AVATAR", turn.getAnswer(), null));
        return turn;
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
