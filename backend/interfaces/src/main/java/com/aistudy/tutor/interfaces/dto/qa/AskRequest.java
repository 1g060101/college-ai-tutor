package com.aistudy.tutor.interfaces.dto.qa;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 提问请求（SSE / 同步共用）
 */
@Data
public class AskRequest {

    @NotBlank(message = "问题不能为空")
    private String question;
}
