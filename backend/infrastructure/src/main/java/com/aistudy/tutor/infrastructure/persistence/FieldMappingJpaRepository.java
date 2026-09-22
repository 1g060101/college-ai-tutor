package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.FieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 字段映射 JPA 仓储（Spring Data）
 */
@Repository
public interface FieldMappingJpaRepository extends JpaRepository<FieldMapping, Long> {
}
