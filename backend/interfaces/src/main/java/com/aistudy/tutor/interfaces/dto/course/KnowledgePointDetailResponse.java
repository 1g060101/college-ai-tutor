package com.aistudy.tutor.interfaces.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 知识点详情响应
 */
@Data
@AllArgsConstructor
public class KnowledgePointDetailResponse {
    private Long id;
    private Long courseId;
    private Long parentId;
    private String name;
    private String description;
    private String tags;
    private int difficulty;
    private BigDecimal weight;
    private int sortOrder;
}
