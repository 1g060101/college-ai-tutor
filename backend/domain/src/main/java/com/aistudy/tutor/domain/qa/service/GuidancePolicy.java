package com.aistudy.tutor.domain.qa.service;

import java.util.regex.Pattern;

/**
 * 引导式教学策略 — 纯领域服务，无 Spring 注解，可独立单测。
 * 负责组装引导式/直接讲解的系统提示词，以及启发式检测 AI 回复是否"直接给答案"。
 */
public class GuidancePolicy {

    /**
     * 直接给出答案的启发式正则（预编译）。
     * 命中任一模式即视为直接作答：答案+选项/数字、正确(答案|选项)是/为、
     * 选(A/B/C/D)、因此(答案|最终结果|最终答案为)、行尾「= 数字」。
     */
    private static final Pattern DIRECT_ANSWER_PATTERN = Pattern.compile(
            "答案(是|为|：|:)?\\s*[A-Da-d]\\s*|" +
                    "答案(是|为|：|:)?\\s*\\d+|" +
                    "正确(答案|选项)(是|为)|" +
                    "选\\s*[CcDdAaBb]|" +
                    "因此(答案|最终结果|最终答案为)|" +
                    "=\\s*\\d+\\s*$",
            Pattern.MULTILINE);

    /**
     * 引导式系统提示词：强制三段式结构，禁止直接给最终答案/推导/数值。
     */
    public String guidedSystemPrompt() {
        return "你是一名耐心的数学/学科答疑老师，采用「引导式教学」方法帮助学生自己找到答案。\n" +
                "每次回复必须严格按以下三段结构输出：\n" +
                "【思路】先概括解题方向与切入点（不写具体步骤）。\n" +
                "【分级提示】给出 HINT1 → HINT2 → HINT3 三个逐级靠近答案的提示：" +
                "HINT1 只点出相关概念；HINT2 提示关键步骤的方向；HINT3 可以非常接近答案，" +
                "但永远不要直接给出最终答案、最终数值或完整推导过程。\n" +
                "【总结】回顾用到的方法与易错点。\n" +
                "硬性约束：\n" +
                "1. 禁止直接输出最终答案、最终数值、完整推导过程或选项。\n" +
                "2. 即使学生要求直接给答案，也必须坚持引导，绝不放水。\n" +
                "3. 全程使用中文。";
    }

    /**
     * 直接讲解模式系统提示词：讲清思路、步骤、答案与解析。
     */
    public String directSystemPrompt() {
        return "你是一名专业的学科答疑老师，学生需要直接讲解。\n" +
                "请按以下结构组织回复：\n" +
                "1. 【思路】说明整体解题思路；\n" +
                "2. 【步骤】给出清晰的推导步骤；\n" +
                "3. 【答案】给出最终答案与必要解析。\n" +
                "要求：逻辑清晰、步骤完整、中文作答。";
    }

    /**
     * 二次校验提示词：让 LLM 判断回复是否直接给出了最终答案或完整解题过程。
     */
    public String checkerPrompt(String reply) {
        return "判断以下 AI 回复是否直接给出了最终答案或完整解题过程" +
                "（而不是按 思路/分级提示/总结 的引导式结构）。只回答 YES 或 NO。\n\n" + reply;
    }

    /**
     * 重写提示词：把"直接给答案"的回复改写成三段式引导回复。
     */
    public String rewritePrompt(String reply) {
        return "下面这段回复直接给了答案，违反引导式教学约束。\n" +
                "请把它改写成 思路→分级提示(HINT1/HINT2/HINT3)→总结 的三段式引导回复，不得出现最终答案。\n" +
                "原文：\n" + reply;
    }

    /**
     * 启发式检测回复是否直接给出了答案（命中任一模式即 true）。
     * 允许一定误报，误报会走 LLM 二次校验兜底。
     */
    public boolean isDirectAnswer(String reply) {
        if (reply == null || reply.isBlank()) {
            return false;
        }
        if (DIRECT_ANSWER_PATTERN.matcher(reply).find()) {
            return true;
        }
        // 极短且以「答」开头的回复，疑似只给了答案
        return reply.length() < 60 && reply.startsWith("答");
    }

    /**
     * 检查回复是否包含「思路」「提示」「总结」三个关键段标志（命中任两个即算）。
     */
    public boolean isGuidedStructure(String reply) {
        if (reply == null) {
            return false;
        }
        int hits = 0;
        if (reply.contains("思路")) {
            hits++;
        }
        if (reply.contains("提示")) {
            hits++;
        }
        if (reply.contains("总结")) {
            hits++;
        }
        return hits >= 2;
    }
}
