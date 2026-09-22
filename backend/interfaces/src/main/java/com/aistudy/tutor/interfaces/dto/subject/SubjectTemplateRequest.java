package com.aistudy.tutor.interfaces.dto.subject;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 配置学科模板请求
 */
@Data
public class SubjectTemplateRequest {

    @NotBlank(message = "学科不能为空")
    private String subject;

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /** 字段映射列表（可空，空模板不建 mapping 行） */
    @Valid
    private List<FieldItem> fields;

    /**
     * 单条字段映射
     */
    @Data
    public static class FieldItem {

        @NotBlank(message = "源字段不能为空")
        private String sourceField;

        @NotBlank(message = "目标字段不能为空")
        private String targetField;
    }
}
