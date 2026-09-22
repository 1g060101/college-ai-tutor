package com.aistudy.tutor.domain.paper.repository;

import com.aistudy.tutor.domain.paper.model.PaperQuestion;

import java.util.List;

/**
 * 试卷题目关联仓储 Port（实现在 infrastructure.persistence）
 */
public interface PaperQuestionRepository {

    PaperQuestion save(PaperQuestion paperQuestion);

    List<PaperQuestion> findByPaperId(Long paperId);
}
