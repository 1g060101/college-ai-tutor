package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.ParsedMaterial;

import java.util.Optional;

/**
 * 解析课件仓储 Port（实现在 infrastructure.persistence）
 */
public interface ParsedMaterialRepository {

    ParsedMaterial save(ParsedMaterial material);

    Optional<ParsedMaterial> findById(Long id);
}
