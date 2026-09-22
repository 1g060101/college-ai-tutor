package com.aistudy.tutor.interfaces.dto.studyplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 生成预习复习计划请求
 */
@Data
public class CreatePlanRequest {

    /** 关联课程 id，可空 */
    private Long courseId;

    @NotBlank(message = "计划标题不能为空")
    @Size(max = 128, message = "计划标题最长 128 个字符")
    private String title;

    @Pattern(regexp = "WEEK|MONTH|EXAM", message = "周期只能是 WEEK/MONTH/EXAM")
    private String period = "WEEK";

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /** 结束日期，可空 */
    private LocalDate endDate;
}
