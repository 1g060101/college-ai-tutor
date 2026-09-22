package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.FieldMapping;
import com.aistudy.tutor.domain.subject.repository.FieldMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 字段映射仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class FieldMappingRepositoryImpl implements FieldMappingRepository {

    private final FieldMappingJpaRepository jpaRepository;

    @Override
    public FieldMapping save(FieldMapping mapping) {
        return jpaRepository.save(mapping);
    }
}
