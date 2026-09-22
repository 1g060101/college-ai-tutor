package com.aistudy.tutor.interfaces.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 同类题推荐响应
 */
@Data
@AllArgsConstructor
public class SimilarQuestionResponse {

    private Long questionId;

    private String content;

    private String type;

    private Integer difficulty;

    /** 知识点掌握度（尚未测评时为 null） */
    private BigDecimal masteryScore;

    private String reasoning;
}
