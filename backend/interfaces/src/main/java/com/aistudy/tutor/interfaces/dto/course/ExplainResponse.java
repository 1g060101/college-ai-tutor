package com.aistudy.tutor.interfaces.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 知识点口语化讲解响应
 */
@Data
@AllArgsConstructor
public class ExplainResponse {
    /** AI 生成的讲解文本 */
    private String text;

    /** 讲解音频地址：恒为 null（TTS 语音合成未配置，预留字段） */
    private String audioUrl;
}
