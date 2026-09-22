package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作业发布请求
 */
@Data
public class PublishAssignmentRequest {

    @NotNull(message = "班级 id 不能为空")
    private Long classId;

    @NotBlank(message = "作业标题不能为空")
    private String title;

    private String description;

    /** 截止时间，可空 */
    private LocalDateTime dueDate;
}
