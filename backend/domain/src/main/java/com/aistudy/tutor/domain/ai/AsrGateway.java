package com.aistudy.tutor.domain.ai;

/**
 * ASR 网关 Port（录音转写）
 */
public interface AsrGateway {

    /**
     * 将音频转写为文字
     *
     * @param audioBase64 音频 base64
     * @param format      音频格式（wav/mp3/m4a）
     * @return 转写文本
     */
    String transcribe(String audioBase64, String format);
}