package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 导入任务 JPA 仓储（Spring Data）
 */
@Repository
public interface ImportJobJpaRepository extends JpaRepository<ImportJob, Long> {
}
