package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.paper.model.HotKpRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 高频考点排行 JPA 仓储（Spring Data）
 */
@Repository
public interface HotKpRankJpaRepository extends JpaRepository<HotKpRank, Long> {
    List<HotKpRank> findByCourseIdAndDeletedFalseOrderByRankDateDesc(Long courseId);
}
