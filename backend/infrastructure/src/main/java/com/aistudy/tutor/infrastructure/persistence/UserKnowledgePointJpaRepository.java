package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.model.UserKnowledgePointId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户知识点掌握度 JPA 仓库
 */
@Repository
public interface UserKnowledgePointJpaRepository extends JpaRepository<UserKnowledgePoint, UserKnowledgePointId> {

    List<UserKnowledgePoint> findById_UserId(Long userId);
}
