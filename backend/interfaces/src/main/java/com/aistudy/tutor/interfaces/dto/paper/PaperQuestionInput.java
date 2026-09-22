package com.aistudy.tutor.interfaces.dto.paper;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 试卷题目输入（题目 id + 分值）
 */
@Data
public class PaperQuestionInput {

    @NotNull(message = "题目 id 不能为空")
    private Long questionId;

    @NotNull(message = "分值不能为空")
    private BigDecimal score;
}
