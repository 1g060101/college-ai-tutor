package com.aistudy.tutor.domain.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户知识点掌握度复合主键（user_id, knowledge_point_id）
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserKnowledgePointId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "knowledge_point_id", nullable = false)
    private Long knowledgePointId;
}
