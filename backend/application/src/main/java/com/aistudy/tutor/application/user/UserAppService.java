package com.aistudy.tutor.application.user;

import com.aistudy.tutor.domain.user.model.User;
import com.aistudy.tutor.domain.user.model.UserRole;
import com.aistudy.tutor.domain.user.repository.UserRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserRepository userRepository;

    @Transactional
    public User register(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("该邮箱已注册");
        }
        // TODO: 密码加密（BCrypt）
        User user = new User(email, password, nickname, UserRole.STUDENT);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }
}