package com.aistudy.tutor.interfaces.dto.paper;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 上传卷面响应
 */
@Data
@AllArgsConstructor
public class CreatePaperResponse {
    private Long paperId;
    private int questionCount;
}
