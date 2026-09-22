package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Assignment;
import com.aistudy.tutor.domain.classroom.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 作业仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class AssignmentRepositoryImpl implements AssignmentRepository {

    private final AssignmentJpaRepository jpaRepository;

    @Override
    public Assignment save(Assignment assignment) {
        return jpaRepository.save(assignment);
    }

    @Override
    public Optional<Assignment> findById(Long id) {
        return jpaRepository.findById(id).filter(a -> !a.isDeleted());
    }
}
