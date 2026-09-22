package com.aistudy.tutor.interfaces.dto.paper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * 上传卷面请求
 */
@Data
public class CreatePaperRequest {

    @NotNull(message = "课程 id 不能为空")
    private Long courseId;

    @NotBlank(message = "试卷标题不能为空")
    private String title;

    /** 卷型：REAL 真题 / MOCK 模拟卷 */
    @Pattern(regexp = "REAL|MOCK", message = "卷型只能是 REAL 或 MOCK")
    private String paperType = "MOCK";

    /** 年份，可空 */
    private Integer year;

    /** 考试时长（分钟），可空 */
    private Integer durationMinutes;

    @NotEmpty(message = "题目列表不能为空")
    @Valid
    private List<PaperQuestionInput> questions;
}
