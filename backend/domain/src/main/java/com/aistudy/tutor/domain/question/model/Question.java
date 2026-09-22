package com.aistudy.tutor.domain.question.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 题目：题库核心实体，挂载到课程 / 知识点，供作答与同类题推荐。
 */
@Getter
@Entity
@Table(name = "question")
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    /** 题型：CHOICE / FILL / BLANK / SUBJECTIVE */
    @Column(length = 20, nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 选项 JSON 字符串 */
    @Column(columnDefinition = "json")
    private String options;

    @Column(columnDefinition = "TEXT")
    private String answer;

    /** 答案解析 */
    @Column(columnDefinition = "TEXT")
    private String analysis;

    /** 难度 1-5，默认 3 */
    @Column(nullable = false, columnDefinition = "tinyint")
    private int difficulty;

    @Column(length = 64)
    private String source;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 作业/题库录入用全字段构造器（source 如「作业」） */
    public Question(Long courseId, Long knowledgePointId, String type, String content,
                    String answer, String analysis, int difficulty, String source) {
        this.courseId = courseId;
        this.knowledgePointId = knowledgePointId;
        this.type = type;
        this.content = content;
        this.answer = answer;
        this.analysis = analysis;
        this.difficulty = difficulty;
        this.source = source;
        this.deleted = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
