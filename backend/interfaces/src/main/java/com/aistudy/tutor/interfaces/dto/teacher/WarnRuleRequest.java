package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设置班级预警规则请求（阈值可配，防误报）
 */
@Data
public class WarnRuleRequest {

    /** 规则类型：FREQUENCY 学习频次 / MASTERY 掌握度 / ABSENCE 缺勤，默认 MASTERY */
    private String ruleType = "MASTERY";

    /** 预警阈值（掌握度百分比等） */
    @NotNull(message = "预警阈值不能为空")
    private BigDecimal threshold;
}