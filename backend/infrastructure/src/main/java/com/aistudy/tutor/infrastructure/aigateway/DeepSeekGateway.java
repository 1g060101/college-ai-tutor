package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.ai.AiGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * DeepSeek 适配器 — OpenAI 兼容 API（https://api.deepseek.com/chat/completions）
 * API Key 走环境变量 DEEPSEEK_API_KEY。
 */
@Slf4j
@Component
public class DeepSeekGateway implements AiGateway {

    private final RestClient restClient;
    private final String model;

    public DeepSeekGateway(
            @Value("${app.ai.deepseek.api-key:}") String apiKey,
            @Value("${app.ai.deepseek.model:deepseek-chat}") String model,
            @Value("${app.ai.deepseek.base-url:https://api.deepseek.com}") String baseUrl) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public AiCallResult chat(String system, List<Map<String, String>> messages, double temperature) {
        var body = requestBody(system, messages, temperature, false);
        Map<?, ?> resp = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        String content = extractContent(resp);
        String modelUsed = resp == null ? model : String.valueOf(resp.get("model"));
        Integer in = resp == null ? null : extractUsage(resp, "prompt_tokens");
        Integer out = resp == null ? null : extractUsage(resp, "completion_tokens");
        return new AiCallResult(UUID.randomUUID().toString(), modelUsed, in, out, content);
    }

    @Override
    public void chatStream(String system, List<Map<String, String>> messages, double temperature,
                           Consumer<String> onChunk, Consumer<AiCallResult> onDone) {
        // 简化流式：单次非流式请求，按句切分增量推送，保证 SSE 接口形态可用。
        // （生产可换 WebClient Flux<ServerSentEvent> 实现真正流式）
        AiCallResult result = chat(system, messages, temperature);
        String content = result.content();
        for (String sentence : content.split("(?<=[。！？!?；;\\n])")) {
            if (!sentence.isBlank()) {
                onChunk.accept(sentence);
            }
        }
        onDone.accept(result);
    }

    @Override
    public String channelName() {
        return "DEEPSEEK";
    }

    private Map<String, Object> requestBody(String system, List<Map<String, String>> messages, double temperature, boolean stream) {
        var sys = Map.<String, String>of("role", "system", "content", system == null ? "" : system);
        return Map.of(
                "model", model,
                "messages", concat(sys, messages),
                "temperature", temperature,
                "stream", stream
        );
    }

    private List<Map<String, String>> concat(Map<String, String> sys, List<Map<String, String>> messages) {
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(sys), messages.stream()).toList();
    }

    private String extractContent(Map<?, ?> resp) {
        try {
            if (resp == null) return "";
            List<?> choices = (List<?>) resp.get("choices");
            if (choices == null || choices.isEmpty()) return "";
            Map<?, ?> first = (Map<?, ?>) choices.get(0);
            Map<?, ?> msg = (Map<?, ?>) first.get("message");
            return msg == null ? "" : String.valueOf(msg.get("content"));
        } catch (Exception e) {
            log.warn("解析 DeepSeek 响应失败: {}", e.getMessage());
            return "";
        }
    }

    private Integer extractUsage(Map<?, ?> resp, String key) {
        try {
            Map<?, ?> usage = (Map<?, ?>) resp.get("usage");
            return usage == null ? null : ((Number) usage.get(key)).intValue();
        } catch (Exception e) {
            return null;
        }
    }
}