package com.aistudy.tutor.interfaces.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 班级信息响应
 */
@Data
@AllArgsConstructor
public class ClassResponse {
    private Long id;
    private String name;
    private String subject;
    private String inviteCode;
    private Long teacherId;
}
