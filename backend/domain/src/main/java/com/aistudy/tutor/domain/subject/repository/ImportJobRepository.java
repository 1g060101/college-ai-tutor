package com.aistudy.tutor.domain.subject.repository;

import com.aistudy.tutor.domain.subject.model.ImportJob;

import java.util.Optional;

/**
 * 导入任务仓储 Port（实现在 infrastructure.persistence）
 */
public interface ImportJobRepository {

    ImportJob save(ImportJob job);

    Optional<ImportJob> findById(Long id);
}
