package com.aistudy.tutor.interfaces.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学生加入班级请求
 */
@Data
public class JoinClassRequest {

    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;
}
