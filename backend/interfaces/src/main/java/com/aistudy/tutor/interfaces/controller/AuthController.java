package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.user.UserAppService;
import com.aistudy.tutor.domain.user.model.User;
import com.aistudy.tutor.interfaces.dto.auth.AuthResponse;
import com.aistudy.tutor.interfaces.dto.auth.LoginRequest;
import com.aistudy.tutor.interfaces.dto.auth.RegisterRequest;
import com.aistudy.tutor.interfaces.security.JwtTokenProvider;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鉴权接口：注册 / 登录
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAppService userAppService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userAppService.register(request.getEmail(), request.getPassword(), request.getNickname(), request.getRole());
        return Result.success(toAuthResponse(user));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userAppService.login(request.getEmail(), request.getPassword());
        return Result.success(toAuthResponse(user));
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getNickname(), user.getRole().name());
    }
}
