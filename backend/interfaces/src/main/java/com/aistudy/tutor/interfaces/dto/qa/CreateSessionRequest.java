package com.aistudy.tutor.interfaces.dto.qa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建答疑会话请求
 */
@Data
public class CreateSessionRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 128, message = "标题最长 128 个字符")
    private String title;

    /** 关联课程 id，可空 */
    private Long courseId;

    /** 回复模式：GUIDED/DIRECT，默认 GUIDED */
    @Pattern(regexp = "GUIDED|DIRECT", message = "回复模式只能是 GUIDED 或 DIRECT")
    private String replyMode = "GUIDED";
}
