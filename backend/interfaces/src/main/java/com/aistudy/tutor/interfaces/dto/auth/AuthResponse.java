package com.aistudy.tutor.interfaces.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录/注册成功响应：携带 JWT 与用户摘要
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long userId;
    private String email;
    private String nickname;
    private String role;
}
