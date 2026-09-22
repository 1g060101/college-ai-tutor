package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.PaperQuestion;
import com.aistudy.tutor.domain.paper.repository.PaperQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 试卷题目关联仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class PaperQuestionRepositoryImpl implements PaperQuestionRepository {

    private final PaperQuestionJpaRepository jpaRepository;

    @Override
    public PaperQuestion save(PaperQuestion paperQuestion) {
        return jpaRepository.save(paperQuestion);
    }

    @Override
    public List<PaperQuestion> findByPaperId(Long paperId) {
        return jpaRepository.findByPaperIdAndDeletedFalseOrderBySortOrderAsc(paperId);
    }
}
