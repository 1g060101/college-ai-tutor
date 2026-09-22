package com.aistudy.tutor.interfaces.dto.paper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 高频考点串讲响应
 */
@Data
@AllArgsConstructor
public class HotPointResponse {

    private Long courseId;

    private List<HotPointItem> points;

    /** AI 生成的串讲文本 */
    private String narrationText;

    /**
     * 高频考点条目
     */
    @Data
    @AllArgsConstructor
    public static class HotPointItem {
        private Long knowledgePointId;
        private String name;
        /** 考察频次（兜底数据时为 0） */
        private Integer examFreq;
        private BigDecimal weight;
    }
}
