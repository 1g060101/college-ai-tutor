package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.WarnRule;
import com.aistudy.tutor.domain.classroom.repository.WarnRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 预警规则仓储 Port 实现
 */
@Component
@RequiredArgsConstructor
public class WarnRuleRepositoryImpl implements WarnRuleRepository {

    private final WarnRuleJpaRepository jpaRepository;

    @Override
    public List<WarnRule> findByTeacherId(Long teacherId) {
        return jpaRepository.findByTeacherIdAndDeletedFalse(teacherId);
    }

    @Override
    public Optional<WarnRule> findActiveByRuleType(Long teacherId, String ruleType) {
        return jpaRepository.findByTeacherIdAndRuleTypeAndDeletedFalse(teacherId, ruleType).stream().findFirst();
    }

    @Override
    public WarnRule save(WarnRule rule) {
        return jpaRepository.save(rule);
    }
}
