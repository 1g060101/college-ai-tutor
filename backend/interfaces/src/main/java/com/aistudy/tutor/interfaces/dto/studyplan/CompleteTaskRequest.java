package com.aistudy.tutor.interfaces.dto.studyplan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 完成任务项请求
 */
@Data
public class CompleteTaskRequest {

    @NotNull(message = "任务 id 不能为空")
    private Long taskId;

    /** 是否答对（驱动艾宾浩斯阶段推进） */
    private boolean correct;

    /** 实际学习时长（秒），可空 */
    private Integer durationSeconds;
}
