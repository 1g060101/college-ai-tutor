package com.aistudy.tutor.domain.paper.repository;

import com.aistudy.tutor.domain.paper.model.HotKpRank;

import java.util.List;

/**
 * 高频考点排行仓储 Port（实现在 infrastructure.persistence）
 */
public interface HotKpRankRepository {

    /** 指定课程的全部排行记录，按统计日期倒序 */
    List<HotKpRank> findByCourseId(Long courseId);
}
