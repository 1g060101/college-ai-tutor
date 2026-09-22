package com.aistudy.tutor.interfaces.security;

import com.aistudy.tutor.domain.user.model.UserRole;
import com.aistudy.tutor.shared.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户上下文工具（供 Controller / 权限校验使用）
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static JwtAuthenticationFilter.LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtAuthenticationFilter.LoginUser user)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return user;
    }

    public static Long currentUserId() {
        return currentUser().userId();
    }

    public static UserRole currentRole() {
        return currentUser().role();
    }

    public static boolean hasRole(UserRole role) {
        return currentUser().role() == role;
    }

    /**
     * 角色断言：不满足则抛 403，供接口层统一调用，禁止在 Controller 手写判断
     */
    public static void requireRole(UserRole role) {
        if (!hasRole(role)) {
            throw new BusinessException(403, "角色无权访问该资源");
        }
    }

    /**
     * 对象级权限断言：仅允许本人操作本人资源（学生/通用）
     */
    public static void requireOwner(Long ownerUserId) {
        if (!currentUserId().equals(ownerUserId)) {
            throw new BusinessException(403, "无权访问该资源");
        }
    }
}