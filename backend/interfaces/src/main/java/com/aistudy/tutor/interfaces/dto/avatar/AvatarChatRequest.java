package com.aistudy.tutor.interfaces.dto.avatar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 陪伴对话请求
 */
@Data
public class AvatarChatRequest {

    /** 关联课程 id，可空 */
    private Long courseId;

    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容最长 2000 个字符")
    private String content;
}
