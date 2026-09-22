package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.ErrorBook;
import com.aistudy.tutor.domain.question.repository.ErrorBookRepository;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 错题本仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class ErrorBookRepositoryImpl implements ErrorBookRepository {

    private final ErrorBookJpaRepository jpaRepository;

    @Override
    public ErrorBook save(ErrorBook errorBook) {
        return jpaRepository.save(errorBook);
    }

    @Override
    public Optional<ErrorBook> findById(Long id) {
        return jpaRepository.findById(id).filter(e -> !e.isDeleted());
    }

    @Override
    public Optional<ErrorBook> findByUserIdAndQuestionId(Long userId, Long questionId) {
        return jpaRepository.findByUserIdAndQuestionIdAndDeletedFalse(userId, questionId);
    }

    @Override
    public PageResult<ErrorBook> pageByUserId(Long userId, Long knowledgePointId, PageQuery query) {
        PageRequest pageRequest = PageRequest.of(query.pageNum() - 1, query.pageSize());
        Page<ErrorBook> page = knowledgePointId == null
                ? jpaRepository.findByUserIdAndDeletedFalse(userId, pageRequest)
                : jpaRepository.findByUserIdAndKnowledgePointIdAndDeletedFalse(userId, knowledgePointId, pageRequest);
        return new PageResult<>(page.getContent(), page.getTotalElements(),
                query.pageNum(), query.pageSize(), PageQuery.totalPages(page.getTotalElements(), query.pageSize()));
    }
}
