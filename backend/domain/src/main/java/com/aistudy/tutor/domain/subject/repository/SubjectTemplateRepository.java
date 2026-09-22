package com.aistudy.tutor.domain.subject.repository;

import com.aistudy.tutor.domain.subject.model.SubjectTemplate;

import java.util.Optional;

/**
 * 学科模板仓储 Port（实现在 infrastructure.persistence）
 */
public interface SubjectTemplateRepository {

    SubjectTemplate save(SubjectTemplate template);

    Optional<SubjectTemplate> findById(Long id);
}
