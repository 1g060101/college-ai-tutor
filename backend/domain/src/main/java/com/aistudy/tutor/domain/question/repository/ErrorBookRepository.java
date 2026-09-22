package com.aistudy.tutor.domain.question.repository;

import com.aistudy.tutor.domain.question.model.ErrorBook;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;

import java.util.Optional;

/**
 * 错题本仓储 Port（实现在 infrastructure.persistence）
 */
public interface ErrorBookRepository {

    ErrorBook save(ErrorBook errorBook);

    Optional<ErrorBook> findById(Long id);

    /** 按用户+题目查询错题记录（不含已删除），用于 upsert */
    Optional<ErrorBook> findByUserIdAndQuestionId(Long userId, Long questionId);

    /** 分页查询当前用户错题本（可按知识点过滤，knowledgePointId 为 null 时不限） */
    PageResult<ErrorBook> pageByUserId(Long userId, Long knowledgePointId, PageQuery query);
}
