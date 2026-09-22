package com.aistudy.tutor.domain.user.repository;

import com.aistudy.tutor.domain.user.model.User;

import java.util.Optional;

/**
 * 用户仓储接口 — 定义在 domain 层，实现在 infrastructure 层
 */
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
}