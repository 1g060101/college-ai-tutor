package com.aistudy.tutor.interfaces.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预警名单响应
 */
@Data
@AllArgsConstructor
public class RiskResponse {

    private Long classId;

    private List<RuleItem> rules;

    private List<RiskItem> risks;

    /**
     * 预警规则条目
     */
    @Data
    @AllArgsConstructor
    public static class RuleItem {
        private String ruleType;
        private BigDecimal threshold;
    }

    /**
     * 预警学生条目
     */
    @Data
    @AllArgsConstructor
    public static class RiskItem {
        private Long studentId;
        private String nickname;
        private List<WeakPointItem> weakPoints;
        private int alertCount;
    }

    /**
     * 薄弱知识点条目
     */
    @Data
    @AllArgsConstructor
    public static class WeakPointItem {
        private String name;
        private BigDecimal masteryScore;
    }
}
