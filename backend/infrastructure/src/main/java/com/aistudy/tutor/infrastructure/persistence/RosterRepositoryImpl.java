package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Roster;
import com.aistudy.tutor.domain.classroom.repository.RosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 班级花名册仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class RosterRepositoryImpl implements RosterRepository {

    private final RosterJpaRepository jpaRepository;

    @Override
    public Roster save(Roster roster) {
        return jpaRepository.save(roster);
    }

    @Override
    public List<Roster> findByClassId(Long classId) {
        return jpaRepository.findByClassIdAndDeletedFalseOrderByJoinTimeAsc(classId);
    }

    @Override
    public boolean existsByClassIdAndStudentId(Long classId, Long studentId) {
        return jpaRepository.existsByClassIdAndStudentIdAndDeletedFalse(classId, studentId);
    }
}
