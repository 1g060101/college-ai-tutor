package com.aistudy.tutor.domain.ai;

/**
 * TTS 网关 Port（语音合成）
 */
public interface TtsGateway {

    /**
     * 文本转语音
     *
     * @param text 文本
     * @param voice 音色
     * @return 音频 base64
     */
    String synthesize(String text, String voice);
}