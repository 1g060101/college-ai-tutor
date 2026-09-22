package com.aistudy.tutor.domain.report.service;

import java.time.LocalDate;

/**
 * 艾宾浩斯记忆曲线领域服务：根据作答表现推进复习阶段并给出下次复习日期。
 * 纯领域服务，无 Spring 注解，可直接单测。
 */
public class EbbinghausService {

    /** 基础复习间隔（天），对应阶段 0-5 */
    private static final int[] BASE_INTERVAL = {1, 2, 4, 7, 15, 30};

    /**
     * 复习安排结果
     */
    public record EbbinghausResult(int currentStage, int repeatIntervalDays, LocalDate nextReviewDate) {}

    /**
     * 学科系数：高数 / 数学 0.9（理解类易遗忘慢），英语 1.1（记忆类需高频复习），其他 1.0。
     * 使用 contains 匹配。
     */
    public double subjectCoefficient(String subject) {
        if (subject == null) {
            return 1.0;
        }
        if (subject.contains("英语")) {
            return 1.1;
        }
        if (subject.contains("数学")) {
            return 0.9;
        }
        return 1.0;
    }

    /**
     * 安排下一次复习：答对阶段 +1（封顶 5），答错重置为 0；
     * 间隔天数 = 基础间隔[阶段] * 学科系数（四舍五入），下次复习日期 = today + 间隔天数。
     */
    public EbbinghausResult schedule(int currentStage, boolean correct, String subject, LocalDate today) {
        int stage = correct ? Math.min(currentStage + 1, 5) : 0;
        int safeStage = Math.max(0, Math.min(stage, BASE_INTERVAL.length - 1));
        int days = (int) Math.round(BASE_INTERVAL[safeStage] * subjectCoefficient(subject));
        return new EbbinghausResult(stage, days, today.plusDays(days));
    }
}
