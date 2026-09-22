package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.question.model.ErrorBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 错题本 JPA 仓储（Spring Data）
 */
@Repository
public interface ErrorBookJpaRepository extends JpaRepository<ErrorBook, Long> {

    Optional<ErrorBook> findByUserIdAndQuestionIdAndDeletedFalse(Long userId, Long questionId);

    Page<ErrorBook> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    Page<ErrorBook> findByUserIdAndKnowledgePointIdAndDeletedFalse(Long userId, Long kpId, Pageable pageable);
}
