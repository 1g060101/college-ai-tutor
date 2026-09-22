package com.aistudy.tutor.interfaces.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 学习路径响应：nodes 为按拓扑顺序排列的知识点
 */
@Data
@AllArgsConstructor
public class LearningPathResponse {
    private Long pathId;
    private Long courseId;
    private String name;
    private List<Node> nodes;

    /**
     * 路径节点：单个知识点摘要
     */
    @Data
    @AllArgsConstructor
    public static class Node {
        private Long id;
        private String name;
        private String tags;
        private int difficulty;
        private Long parentId;
    }
}
