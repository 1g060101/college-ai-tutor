package com.aistudy.tutor.domain.ai;

import java.util.List;
import java.util.Map;

/**
 * AI 网关 Port — 定义在 domain 层，实现在 infrastructure 层（依赖倒置）
 * 换 AI 厂商只改 infrastructure 的适配器实现。
 */
public interface AiGateway {

    /**
     * 非流式对话
     *
     * @param system  系统提示词
     * @param messages 用户消息列表（按时间顺序）
     * @param temperature 采样温度 0-2
     * @return 完整回复
     */
    AiCallResult chat(String system, List<Map<String, String>> messages, double temperature);

    /**
     * 流式对话（SSE 用）— 以回调方式推送增量
     */
    void chatStream(String system, List<Map<String, String>> messages, double temperature,
                    java.util.function.Consumer<String> onChunk, java.util.function.Consumer<AiCallResult> onDone);

    /**
     * 通道名称（DEEPSEEK / MOONSHOT ...），用于审计与 failover
     */
    String channelName();
}