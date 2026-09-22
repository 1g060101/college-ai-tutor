package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 班级花名册 JPA 仓储（Spring Data）
 */
@Repository
public interface RosterJpaRepository extends JpaRepository<Roster, Long> {

    List<Roster> findByClassIdAndDeletedFalseOrderByJoinTimeAsc(Long classId);

    boolean existsByClassIdAndStudentIdAndDeletedFalse(Long classId, Long studentId);
}
