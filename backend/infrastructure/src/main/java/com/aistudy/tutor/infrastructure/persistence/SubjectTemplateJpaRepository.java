package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.SubjectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 学科模板 JPA 仓储（Spring Data）
 */
@Repository
public interface SubjectTemplateJpaRepository extends JpaRepository<SubjectTemplate, Long> {
}
