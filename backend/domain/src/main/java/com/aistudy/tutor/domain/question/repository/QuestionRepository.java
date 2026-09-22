package com.aistudy.tutor.domain.question.repository;

import com.aistudy.tutor.domain.question.model.Question;

import java.util.List;
import java.util.Optional;

/**
 * 题目仓库 Port
 */
public interface QuestionRepository {

    Optional<Question> findById(Long id);

    /** 保存题目（作业批量录入使用） */
    Question save(Question question);

    /** 指定知识点的全部题目（不含已删除） */
    List<Question> findByKnowledgePointId(Long kpId);

    /** 指定知识点下排除某题的其他题目（不含已删除） */
    List<Question> findByKnowledgePointIdAndIdNot(Long kpId, Long excludeId);

    /** 指定课程下排除某题的其他题目（不含已删除） */
    List<Question> findByCourseIdAndDeletedFalseAndIdNot(Long courseId, Long id);
}
