package com.aistudy.tutor.interfaces.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 班级成员名单响应
 */
@Data
@AllArgsConstructor
public class RosterResponse {

    private Long classId;

    private String className;

    private List<StudentItem> students;

    /**
     * 班级成员条目
     */
    @Data
    @AllArgsConstructor
    public static class StudentItem {
        private Long studentId;
        private String nickname;
    }
}
