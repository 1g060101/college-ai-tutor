package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建班级请求
 */
@Data
public class CreateClassRequest {

    @NotBlank(message = "班级名称不能为空")
    private String name;

    /** 科目，可空 */
    private String subject;
}
