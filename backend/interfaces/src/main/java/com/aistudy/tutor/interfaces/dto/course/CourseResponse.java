package com.aistudy.tutor.interfaces.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 课程列表响应
 */
@Data
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String name;
    private String subject;
    private String description;
    private int difficulty;
}
