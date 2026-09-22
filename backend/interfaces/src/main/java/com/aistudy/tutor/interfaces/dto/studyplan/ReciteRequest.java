package com.aistudy.tutor.interfaces.dto.studyplan;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 抽背/默写/公式速记判分请求
 */
@Data
public class ReciteRequest {

    /** 知识点 id，可空 */
    private Long kpId;

    /** 课程 id，可空 */
    private Long courseId;

    @NotBlank(message = "作答内容不能为空")
    private String content;

    @NotBlank(message = "标准答案不能为空")
    private String expected;
}
