package com.aistudy.tutor.interfaces.dto.paper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 答题技巧/时间分配响应
 */
@Data
@AllArgsConstructor
public class ExamTipsResponse {
    private List<String> tips;
    private List<String> disclaimers;
}
