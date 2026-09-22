package com.aistudy.tutor.interfaces.dto.qa;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 答疑反馈请求
 */
@Data
public class FeedbackRequest {

    @NotNull(message = "轮次 id 不能为空")
    private Long turnId;

    /** 用户反馈文本，可空 */
    private String feedback;

    /** 是否对讲解有帮助，默认 true */
    private boolean helpful = true;

    /** 回答是否正确，默认 true */
    private boolean correct = true;
}
