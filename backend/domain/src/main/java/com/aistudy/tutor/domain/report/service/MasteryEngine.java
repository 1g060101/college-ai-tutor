package com.aistudy.tutor.domain.report.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 掌握度引擎：把若干次作答事件加权聚合成 0-100 掌握度，用 EWMA 平滑防震荡。
 * 纯领域服务，无 Spring 注解，可直接单测。
 */
public class MasteryEngine {

    /** EWMA 平滑系数：新事件权重 */
    private static final double ALPHA = 0.3;

    /**
     * 单次作答事件的输入（调用方按时间升序传入）
     */
    public record EventInput(boolean correct, BigDecimal score, int difficulty, String source) {}

    /**
     * 来源权重：QA 答疑 / HOMEWORK 作业 / EXAM 考试 / STUDY 自学，未知来源取 0.6。
     * 考试可信度最高，自学最低。
     */
    public double sourceWeight(String source) {
        if (source == null) {
            return 0.6;
        }
        return switch (source) {
            case "QA" -> 0.6;
            case "HOMEWORK" -> 0.8;
            case "EXAM" -> 1.0;
            case "STUDY" -> 0.5;
            default -> 0.6;
        };
    }

    /**
     * 单次事件得分：正确取分数（缺省 100），错误为 0，再乘难度系数 (0.8 + difficulty*0.05)。
     * score 为百分制 0-100。
     */
    public double eventScore(boolean correct, BigDecimal score, int difficulty) {
        double base = correct ? (score != null ? score.doubleValue() : 100.0) : 0.0;
        double factor = 0.8 + difficulty * 0.05;
        return base * factor;
    }

    /**
     * 按时间序（调用方保证升序）EWMA 聚合：
     * 事件得分 = eventScore * sourceWeight（体现来源可信度加权）；
     * newMastery = alpha * 事件得分 + (1 - alpha) * oldMastery，首事件直接用事件得分。
     * 返回 0-100，四舍五入保留 1 位小数。
     */
    public double aggregate(List<EventInput> events) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }
        double mastery = 0.0;
        boolean first = true;
        for (EventInput event : events) {
            double eventScore = eventScore(event.correct(), event.score(), event.difficulty())
                    * sourceWeight(event.source());
            mastery = first ? eventScore : ALPHA * eventScore + (1 - ALPHA) * mastery;
            first = false;
        }
        double clamped = Math.max(0.0, Math.min(100.0, mastery));
        return Math.round(clamped * 10) / 10.0;
    }
}
