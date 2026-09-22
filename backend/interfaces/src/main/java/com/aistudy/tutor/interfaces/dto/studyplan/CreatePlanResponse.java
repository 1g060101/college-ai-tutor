package com.aistudy.tutor.interfaces.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 生成计划响应
 */
@Data
@AllArgsConstructor
public class CreatePlanResponse {
    private Long planId;
    private int taskCount;
}
