package com.aistudy.tutor.domain.studyplan.repository;

import com.aistudy.tutor.domain.studyplan.model.StudyRecord;

/**
 * 学习记录仓储 Port（实现在 infrastructure.persistence）
 */
public interface StudyRecordRepository {

    StudyRecord save(StudyRecord record);
}
