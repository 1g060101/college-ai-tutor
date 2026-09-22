package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.Summary;

/**
 * 要点提炼仓储 Port（实现在 infrastructure.persistence）
 */
public interface SummaryRepository {

    Summary save(Summary summary);
}
