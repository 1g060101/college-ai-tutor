package com.aistudy.tutor.interfaces.dto.homework;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作业作答提交请求
 */
@Data
public class SubmitAssignmentRequest {

    /** 作答内容（主观题文本，可空） */
    private String answerContent;

    /** 是否答对 */
    private boolean correct;

    /** 得分 0-100（可空，缺省时正确记 100 / 错误记 0） */
    private BigDecimal score;

    /** 作答耗时（秒，可空） */
    private Integer durationSeconds;

    /** 幂等键（可空，缺省服务端生成 UUID） */
    private String requestId;
}
