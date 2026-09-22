package com.aistudy.tutor.interfaces.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * 今日回顾任务响应
 */
@Data
@AllArgsConstructor
public class TodayTaskResponse {
    private Long planId;
    private Long taskId;
    private String title;
    private String taskType;
    private Long kpId;
    private LocalDate nextReviewDate;
}
