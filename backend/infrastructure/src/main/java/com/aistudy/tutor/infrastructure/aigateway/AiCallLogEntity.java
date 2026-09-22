package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 调用审计日志实体（对应 ai_call_log 表）
 */
@Getter
@Entity
@Table(name = "ai_call_log")
@NoArgsConstructor
public class AiCallLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 64)
    private String requestId;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 20)
    private String channel;

    @Column(length = 64)
    private String model;

    @Column(name = "tokens_in")
    private Integer tokensIn;

    @Column(name = "tokens_out")
    private Integer tokensOut;

    private java.math.BigDecimal cost;

    @Column(nullable = false)
    private boolean success = true;

    @Column(name = "error_msg", length = 512)
    private String errorMsg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static AiCallLogEntity of(Long userId, String channel, AiCallResult result, boolean success, String errorMsg) {
        AiCallLogEntity e = new AiCallLogEntity();
        e.userId = userId;
        e.channel = channel;
        if (result != null) {
            e.requestId = result.requestId();
            e.model = result.model();
            e.tokensIn = result.tokensIn();
            e.tokensOut = result.tokensOut();
            e.cost = java.math.BigDecimal.valueOf(result.estimatedCost());
        } else {
            e.requestId = java.util.UUID.randomUUID().toString();
        }
        e.success = success;
        e.errorMsg = errorMsg;
        return e;
    }
}