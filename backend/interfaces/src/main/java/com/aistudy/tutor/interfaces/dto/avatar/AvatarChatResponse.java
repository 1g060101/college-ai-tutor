package com.aistudy.tutor.interfaces.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 陪伴对话响应：AI 回复文本 + 答疑会话 id
 */
@Data
@AllArgsConstructor
public class AvatarChatResponse {
    private String reply;
    private Long sessionId;
}
