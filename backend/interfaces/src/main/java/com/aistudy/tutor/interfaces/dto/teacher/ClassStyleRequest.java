package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 班级数字人教学风格配置请求
 */
@Data
public class ClassStyleRequest {

    @NotBlank(message = "语气不能为空")
    private String tone;

    @NotBlank(message = "风格不能为空")
    private String style;
}
