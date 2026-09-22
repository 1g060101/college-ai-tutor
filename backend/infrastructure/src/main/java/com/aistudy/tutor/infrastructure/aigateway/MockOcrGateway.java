package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.domain.ai.OcrGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OCR 网关桩实现：未配置 OCR 服务时返回 null，由调用方降级为手动输入。
 * 配置真实 OCR 后替换本实现即可，不改业务代码。
 */
@Slf4j
@Component
public class MockOcrGateway implements OcrGateway {

    @Override
    public String recognize(String imageBase64) {
        log.info("未配置 OCR 服务，跳过图片识别（降级手动输入）");
        return null;
    }
}
