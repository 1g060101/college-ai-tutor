package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.SchoolClass;

import java.util.Optional;

/**
 * 班级仓储 Port（实现在 infrastructure.persistence）
 */
public interface SchoolClassRepository {

    SchoolClass save(SchoolClass schoolClass);

    Optional<SchoolClass> findById(Long id);

    Optional<SchoolClass> findByInviteCode(String inviteCode);
}
