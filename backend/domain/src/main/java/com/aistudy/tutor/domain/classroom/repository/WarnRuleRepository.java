package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.WarnRule;

import java.util.List;
import java.util.Optional;

/**
 * 预警规则仓储 Port（实现在 infrastructure.persistence）
 */
public interface WarnRuleRepository {

    List<WarnRule> findByTeacherId(Long teacherId);

    /** 查询某教师某类型未删除的预警规则（通常单条） */
    Optional<WarnRule> findActiveByRuleType(Long teacherId, String ruleType);

    WarnRule save(WarnRule rule);
}
