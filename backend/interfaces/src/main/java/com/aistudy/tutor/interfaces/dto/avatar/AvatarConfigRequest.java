package com.aistudy.tutor.interfaces.dto.avatar;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 数字人配置更新请求（PUT，upsert）
 */
@Data
public class AvatarConfigRequest {

    @Size(max = 64, message = "数字人名称最长 64 个字符")
    private String avatarName;

    @Pattern(regexp = "FRIENDLY|STRICT|CASUAL", message = "语气只能是 FRIENDLY/STRICT/CASUAL")
    private String tone;

    @Pattern(regexp = "CONCISE|DETAILED|MOTIVATIONAL", message = "风格只能是 CONCISE/DETAILED/MOTIVATIONAL")
    private String style;

    @Size(max = 20, message = "音色最长 20 个字符")
    private String voice;
}
