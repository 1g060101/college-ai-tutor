package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目 JPA 仓库
 */
@Repository
public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    List<Question> findByKnowledgePointIdAndDeletedFalse(Long kpId);

    List<Question> findByKnowledgePointIdAndDeletedFalseAndIdNot(Long kpId, Long id);

    List<Question> findByCourseIdAndDeletedFalseAndIdNot(Long courseId, Long id);
}
