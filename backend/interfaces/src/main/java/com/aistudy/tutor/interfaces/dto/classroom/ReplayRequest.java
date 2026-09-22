package com.aistudy.tutor.interfaces.dto.classroom;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 片段重讲请求
 */
@Data
public class ReplayRequest {

    @NotBlank(message = "关键词不能为空")
    private String keyword;
}
