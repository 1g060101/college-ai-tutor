package com.aistudy.tutor.interfaces.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 抽背/默写/公式速记判分响应
 */
@Data
@AllArgsConstructor
public class ReciteResponse {
    private boolean correct;
    private int score;
    private String matchedContent;
}
