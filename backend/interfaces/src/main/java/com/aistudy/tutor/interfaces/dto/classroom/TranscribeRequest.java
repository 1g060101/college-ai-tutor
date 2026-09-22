package com.aistudy.tutor.interfaces.dto.classroom;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 录音转文字请求（演示环境无 ASR 实现，直接接收用户粘贴的转写文本）
 */
@Data
public class TranscribeRequest {

    /** 关联课程 id，可空 */
    private Long courseId;

    /** 课堂名称，可空 */
    private String lectureName;

    @NotBlank(message = "转写文本不能为空")
    private String transcriptText;
}
