package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 知识库维护请求（新增知识点）
 */
@Data
public class KnowledgePointRequest {

    /** 所属课程 id，可空 */
    private Long courseId;

    @NotBlank(message = "知识点名称不能为空")
    private String name;

    /** 标签，逗号分隔，可空 */
    private String tags;

    /** 难度 1-5，默认 3 */
    private Integer difficulty;

    /** 权重，默认 1.00 */
    private BigDecimal weight;

    private String description;
}
