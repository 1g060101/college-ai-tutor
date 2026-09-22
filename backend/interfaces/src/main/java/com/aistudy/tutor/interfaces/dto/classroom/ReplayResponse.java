package com.aistudy.tutor.interfaces.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 片段重讲响应
 */
@Data
@AllArgsConstructor
public class ReplayResponse {

    /** 定位到的原始课堂片段 */
    private String fragment;

    /** AI 口语化重讲内容 */
    private String explanation;
}
