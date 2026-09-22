package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 班级 JPA 仓储（Spring Data）
 */
@Repository
public interface SchoolClassJpaRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByInviteCodeAndDeletedFalse(String inviteCode);
}
