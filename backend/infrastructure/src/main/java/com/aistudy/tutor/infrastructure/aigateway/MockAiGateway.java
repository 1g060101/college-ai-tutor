package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.ai.AiGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Mock AI 网关兜底适配器：当未配置真实 DeepSeek API Key 时生效（failover 链末位）。
 * 返回确定的引导式（GUIDED）三段回复，保证「答疑 → 掌握度 → 报告」闭环在无 Key 环境可演示；
 * 配置真实 Key 后本 Bean 自动失效，走 DeepSeekGateway。
 */
@Component
@ConditionalOnProperty(name = "app.ai.deepseek.api-key", havingValue = "", matchIfMissing = true)
public class MockAiGateway implements AiGateway {

    private static final String GUIDED_REPLY = """
            【思路】本题考查基本概念的理解与运用。建议先回顾相关定义，再结合题目给出的已知条件，把条件逐条映射到定义或公式上，逐步缩小范围。不要急着写答案，先理清每一步的依据。

            【分级提示】
            HINT1：题目给出的已知条件分别对应哪些概念或公式？先列出来。
            HINT2：尝试把条件代入对应公式，观察能得到什么中间结果，注意单位与符号是否一致。
            HINT3：检查中间结果是否满足题目要求；若存在多个方向，请结合条件排除，并说出你的理由。

            【总结】这类题的关键是抓住「定义—条件—公式」的对应关系。建议你动笔把推演过程写一遍，卡在哪一步随时向我追问，我会继续给你下一步提示。
            """;

    private static final String DIRECT_REPLY = """
            【讲解】这道题按以下思路直接给出解析：
            第一步：明确已知条件与目标；
            第二步：选用对应公式，把已知量代入；
            第三步：化简计算得到结果，并回代验证。
            【答案】按上述方法计算，最终结果为：42（演示数据）。
            """;

    private static final String GUIDED_CORRECTED_REPLY = """
            【思路】这道题不建议直接给答案，先看它考察的知识点与基本方法。

            【分级提示】
            HINT1：先确认题目考察的知识点是什么。
            HINT2：回忆该知识点的标准解题步骤。
            HINT3：把题目条件套入步骤，逐步得到结果。

            【总结】按提示自己推演一遍，能更好地掌握这类题目。
            """;

    @Override
    public AiCallResult chat(String system, List<Map<String, String>> messages, double temperature) {
        String userContent = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getOrDefault("content", "");
        String content;
        if (userContent.contains("只回答 YES 或 NO")) {
            content = "NO";
        } else if (userContent.contains("请把它改写成")) {
            content = GUIDED_CORRECTED_REPLY;
        } else if (system != null && system.contains("直接讲解模式")) {
            content = DIRECT_REPLY;
        } else {
            content = GUIDED_REPLY;
        }
        return new AiCallResult(UUID.randomUUID().toString(), "mock", 50, 200, content);
    }

    @Override
    public void chatStream(String system, List<Map<String, String>> messages, double temperature,
                           Consumer<String> onChunk, Consumer<AiCallResult> onDone) {
        AiCallResult result = chat(system, messages, temperature);
        for (String sentence : result.content().split("(?<=[。！？!?；;\\n])")) {
            if (!sentence.isBlank()) {
                onChunk.accept(sentence);
            }
        }
        onDone.accept(result);
    }

    @Override
    public String channelName() {
        return "MOCK";
    }
}
