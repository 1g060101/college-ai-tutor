package com.aistudy.tutor.interfaces.dto.qa;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单轮对话响应
 */
@Data
@AllArgsConstructor
public class TurnResponse {
    private Long id;
    private Long sessionId;
    private String question;
    private String answer;
    private Long kpHit;
    private String guidanceLevel;
    private String feedback;
    private LocalDateTime createdAt;
}
