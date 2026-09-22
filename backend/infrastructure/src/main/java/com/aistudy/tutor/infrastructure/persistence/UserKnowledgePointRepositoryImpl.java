package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.model.UserKnowledgePointId;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 用户知识点掌握度仓库 Port 实现
 */
@Component
@RequiredArgsConstructor
public class UserKnowledgePointRepositoryImpl implements UserKnowledgePointRepository {

    private final UserKnowledgePointJpaRepository jpaRepository;

    @Override
    public Optional<UserKnowledgePoint> findById(UserKnowledgePointId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public UserKnowledgePoint save(UserKnowledgePoint userKnowledgePoint) {
        return jpaRepository.save(userKnowledgePoint);
    }

    @Override
    public List<UserKnowledgePoint> findByUserId(Long userId) {
        return jpaRepository.findById_UserId(userId);
    }
}
