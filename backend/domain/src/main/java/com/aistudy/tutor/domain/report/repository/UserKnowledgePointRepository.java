package com.aistudy.tutor.domain.report.repository;

import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.model.UserKnowledgePointId;

import java.util.List;
import java.util.Optional;

/**
 * 用户知识点掌握度仓库 Port
 */
public interface UserKnowledgePointRepository {

    Optional<UserKnowledgePoint> findById(UserKnowledgePointId id);

    UserKnowledgePoint save(UserKnowledgePoint userKnowledgePoint);

    List<UserKnowledgePoint> findByUserId(Long userId);
}
