package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.ParsedMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 解析课件 JPA 仓储（Spring Data）
 */
@Repository
public interface ParsedMaterialJpaRepository extends JpaRepository<ParsedMaterial, Long> {
}
