package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.Paper;
import com.aistudy.tutor.domain.paper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 试卷仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class PaperRepositoryImpl implements PaperRepository {

    private final PaperJpaRepository jpaRepository;

    @Override
    public Paper save(Paper paper) {
        return jpaRepository.save(paper);
    }

    @Override
    public Optional<Paper> findById(Long id) {
        return jpaRepository.findById(id).filter(p -> !p.isDeleted());
    }
}
