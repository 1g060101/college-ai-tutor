package com.aistudy.tutor.interfaces.dto.homework;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 上传/创建作业请求
 */
@Data
public class CreateAssignmentRequest {

    @NotNull(message = "课程 id 不能为空")
    private Long courseId;

    @NotEmpty(message = "题目列表不能为空")
    @Valid
    private List<QuestionInput> questions;

    /**
     * 作业题目入参
     */
    @Data
    public static class QuestionInput {

        @NotBlank(message = "题目内容不能为空")
        private String content;

        /** 题型：CHOICE / FILL_BLANK / SUBJECTIVE，默认 SUBJECTIVE */
        private String type = "SUBJECTIVE";

        /** 参考答案，可空 */
        private String answer;

        /** 答案解析，可空 */
        private String analysis;

        /** 难度 1-5，可空（缺省按题目长度启发式估算） */
        private Integer difficulty;

        /** 关联知识点 id，可空 */
        private Long knowledgePointId;
    }
}
