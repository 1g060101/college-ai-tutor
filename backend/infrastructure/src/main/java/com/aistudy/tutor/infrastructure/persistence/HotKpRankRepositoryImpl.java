package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.HotKpRank;
import com.aistudy.tutor.domain.paper.repository.HotKpRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 高频考点排行仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class HotKpRankRepositoryImpl implements HotKpRankRepository {

    private final HotKpRankJpaRepository jpaRepository;

    @Override
    public List<HotKpRank> findByCourseId(Long courseId) {
        return jpaRepository.findByCourseIdAndDeletedFalseOrderByRankDateDesc(courseId);
    }
}
