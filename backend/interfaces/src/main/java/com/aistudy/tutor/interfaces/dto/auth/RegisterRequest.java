package com.aistudy.tutor.interfaces.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需在 6-32 位之间")
    @Pattern(regexp = "^[A-Za-z0-9@#$%^&*!._-]+$", message = "密码只能包含字母、数字及常见符号")
    private String password;

    @Size(max = 32, message = "昵称最长 32 个字符")
    private String nickname;

    /** 注册角色：仅支持 STUDENT（默认）/ TEACHER，禁止自注册 ADMIN */
    private String role;
}
