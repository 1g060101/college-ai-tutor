package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.Question;
import com.aistudy.tutor.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 题目仓库 Port 实现
 */
@Component
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final QuestionJpaRepository jpaRepository;

    @Override
    public Optional<Question> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Question> findByKnowledgePointId(Long kpId) {
        return jpaRepository.findByKnowledgePointIdAndDeletedFalse(kpId);
    }

    @Override
    public List<Question> findByKnowledgePointIdAndIdNot(Long kpId, Long excludeId) {
        return jpaRepository.findByKnowledgePointIdAndDeletedFalseAndIdNot(kpId, excludeId);
    }

    @Override
    public List<Question> findByCourseIdAndDeletedFalseAndIdNot(Long courseId, Long id) {
        return jpaRepository.findByCourseIdAndDeletedFalseAndIdNot(courseId, id);
    }
}
