package com.aistudy.tutor.application.user;

import com.aistudy.tutor.domain.user.model.User;
import com.aistudy.tutor.domain.user.model.UserRole;
import com.aistudy.tutor.domain.user.repository.UserRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户应用服务：注册 / 登录 / 查询
 */
@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 注册（默认 STUDENT 角色，密码 BCrypt 加密）
     */
    @Transactional
    public User register(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(400, "该邮箱已注册");
        }
        String encoded = passwordEncoder.encode(password);
        User user = new User(email, encoded, nickname, UserRole.STUDENT);
        return userRepository.save(user);
    }

    /**
     * 登录：校验邮箱与密码，返回用户（令牌由接口层生成）
     */
    @Transactional(readOnly = true)
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(401, "邮箱或密码错误"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(401, "邮箱或密码错误");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }
}
