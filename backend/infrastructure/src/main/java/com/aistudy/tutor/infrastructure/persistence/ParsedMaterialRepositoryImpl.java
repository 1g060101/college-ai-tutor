package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.ParsedMaterial;
import com.aistudy.tutor.domain.classroom.repository.ParsedMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 解析课件仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class ParsedMaterialRepositoryImpl implements ParsedMaterialRepository {

    private final ParsedMaterialJpaRepository jpaRepository;

    @Override
    public ParsedMaterial save(ParsedMaterial material) {
        return jpaRepository.save(material);
    }

    @Override
    public Optional<ParsedMaterial> findById(Long id) {
        return jpaRepository.findById(id).filter(m -> !m.isDeleted());
    }
}
