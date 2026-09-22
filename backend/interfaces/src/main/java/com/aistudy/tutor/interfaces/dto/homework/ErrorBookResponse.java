package com.aistudy.tutor.interfaces.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题本条目响应
 */
@Data
@AllArgsConstructor
public class ErrorBookResponse {
    private Long id;
    private Long questionId;
    /** 题目内容 */
    private String content;
    private Long knowledgePointId;
    /** 知识点名称 */
    private String kpName;
    private String wrongAnswer;
    private String errorReason;
    private Integer errorCount;
    private Boolean mastered;
    private LocalDateTime lastWrongAt;
}
