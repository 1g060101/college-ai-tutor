package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 试卷 JPA 仓储（Spring Data）
 */
@Repository
public interface PaperJpaRepository extends JpaRepository<Paper, Long> {
}
