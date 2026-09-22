package com.aistudy.tutor.domain.ai;

/**
 * OCR 网关 Port（拍照搜题）
 */
public interface OcrGateway {

    /**
     * 识别图片中的文字
     *
     * @param imageBase64 图片 base64
     * @return 识别文本；置信度不足时返回 null（调用方降级手动输入）
     */
    String recognize(String imageBase64);
}