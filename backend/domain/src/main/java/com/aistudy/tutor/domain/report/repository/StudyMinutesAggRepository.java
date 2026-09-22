package com.aistudy.tutor.domain.report.repository;

import com.aistudy.tutor.domain.report.model.StudyMinutesAgg;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习时长聚合仓库 Port
 */
public interface StudyMinutesAggRepository {

    List<StudyMinutesAgg> findByUserIdAndStatDateBetween(Long userId, LocalDate from, LocalDate to);
}
