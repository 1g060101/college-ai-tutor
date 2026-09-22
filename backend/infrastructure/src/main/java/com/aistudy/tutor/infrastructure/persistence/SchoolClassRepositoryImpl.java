package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.SchoolClass;
import com.aistudy.tutor.domain.classroom.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 班级仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class SchoolClassRepositoryImpl implements SchoolClassRepository {

    private final SchoolClassJpaRepository jpaRepository;

    @Override
    public SchoolClass save(SchoolClass schoolClass) {
        return jpaRepository.save(schoolClass);
    }

    @Override
    public Optional<SchoolClass> findById(Long id) {
        return jpaRepository.findById(id).filter(c -> !c.isDeleted());
    }

    @Override
    public Optional<SchoolClass> findByInviteCode(String inviteCode) {
        return jpaRepository.findByInviteCodeAndDeletedFalse(inviteCode);
    }
}
