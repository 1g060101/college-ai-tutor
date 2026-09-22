package com.aistudy.tutor.interfaces.dto.paper;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 整卷讲解响应
 */
@Data
@AllArgsConstructor
public class PaperAnalysisResponse {
    private Long paperId;
    private String title;
    private String analysisText;
}
