package com.aistudy.tutor.domain.paper.repository;

import com.aistudy.tutor.domain.paper.model.Paper;

import java.util.Optional;

/**
 * 试卷仓储 Port（实现在 infrastructure.persistence）
 */
public interface PaperRepository {

    Paper save(Paper paper);

    Optional<Paper> findById(Long id);
}
