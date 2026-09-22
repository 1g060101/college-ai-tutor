package com.aistudy.tutor.domain.subject.repository;

import com.aistudy.tutor.domain.subject.model.FieldMapping;

/**
 * 字段映射仓储 Port（实现在 infrastructure.persistence）
 */
public interface FieldMappingRepository {

    FieldMapping save(FieldMapping mapping);
}
