package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.ai.AiGateway;
import com.aistudy.tutor.domain.ai.OcrGateway;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * AI 调用门面 — 统一限流、审计、失败降级。
 * 上层（application）只依赖本服务，不直接触碰具体网关。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCallFacade {

    private final List<AiGateway> chatGateways;
    private final AiRateLimiter rateLimiter;
    private final AiCallLogRepository callLogRepository;

    public AiCallResult chat(Long userId, String system, List<Map<String, String>> messages, double temperature) {
        rateLimiter.acquire(userId);
        try {
            // failover：主备通道依次尝试
            for (AiGateway gateway : chatGateways) {
                try {
                    AiCallResult result = gateway.chat(system, messages, temperature);
                    saveLog(userId, gateway.channelName(), result, true, null);
                    return result;
                } catch (Exception e) {
                    log.warn("AI 通道 {} 调用失败: {}", gateway.channelName(), e.getMessage());
                    saveLog(userId, gateway.channelName(), null, false, e.getMessage());
                }
            }
            throw new BusinessException(502, "AI 服务暂时不可用，请稍后再试");
        } finally {
            rateLimiter.release(userId);
        }
    }

    public void chatStream(Long userId, String system, List<Map<String, String>> messages, double temperature,
                           Consumer<String> onChunk, Consumer<AiCallResult> onDone) {
        rateLimiter.acquire(userId);
        try {
            for (AiGateway gateway : chatGateways) {
                try {
                    gateway.chatStream(system, messages, temperature, onChunk, result -> {
                        saveLog(userId, gateway.channelName(), result, true, null);
                        onDone.accept(result);
                    });
                    return;
                } catch (Exception e) {
                    log.warn("AI 通道 {} 流式调用失败: {}", gateway.channelName(), e.getMessage());
                    saveLog(userId, gateway.channelName(), null, false, e.getMessage());
                }
            }
            throw new BusinessException(502, "AI 服务暂时不可用，请稍后再试");
        } finally {
            rateLimiter.release(userId);
        }
    }

    private void saveLog(Long userId, String channel, AiCallResult result, boolean success, String errorMsg) {
        try {
            callLogRepository.save(AiCallLogEntity.of(userId, channel, result, success, errorMsg));
        } catch (Exception e) {
            log.warn("AI 审计日志保存失败: {}", e.getMessage());
        }
    }
}