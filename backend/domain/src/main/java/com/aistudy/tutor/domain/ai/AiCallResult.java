package com.aistudy.tutor.domain.ai;

import java.util.List;

/**
 * AI 网关结果 — 包含请求元数据（requestId/model/tokens/cost）供审计
 */
public record AiCallResult(
        String requestId,
        String model,
        Integer tokensIn,
        Integer tokensOut,
        String content
) {

    public double estimatedCost() {
        // 简化计价：DeepSeek 约 0.14元/百万 input token，0.28元/百万 output token
        double in = tokensIn == null ? 0 : tokensIn * 0.14 / 1_000_000;
        double out = tokensOut == null ? 0 : tokensOut * 0.28 / 1_000_000;
        return in + out;
    }
}