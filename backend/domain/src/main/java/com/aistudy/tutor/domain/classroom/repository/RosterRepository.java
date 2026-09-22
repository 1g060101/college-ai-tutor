package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.Roster;

import java.util.List;

/**
 * 班级花名册仓储 Port（实现在 infrastructure.persistence）
 */
public interface RosterRepository {

    Roster save(Roster roster);

    List<Roster> findByClassId(Long classId);

    boolean existsByClassIdAndStudentId(Long classId, Long studentId);
}
