package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷题目关联 JPA 仓储（Spring Data）
 */
@Repository
public interface PaperQuestionJpaRepository extends JpaRepository<PaperQuestion, Long> {
    List<PaperQuestion> findByPaperIdAndDeletedFalseOrderBySortOrderAsc(Long paperId);
}
