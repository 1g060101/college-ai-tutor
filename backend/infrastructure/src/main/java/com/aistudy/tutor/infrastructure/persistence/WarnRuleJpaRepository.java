package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.WarnRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预警规则 JPA 仓储（Spring Data）
 */
@Repository
public interface WarnRuleJpaRepository extends JpaRepository<WarnRule, Long> {
    List<WarnRule> findByTeacherIdAndDeletedFalse(Long teacherId);

    List<WarnRule> findByTeacherIdAndRuleTypeAndDeletedFalse(Long teacherId, String ruleType);
}
