package com.aistudy.tutor.interfaces.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 数字人配置响应
 */
@Data
@AllArgsConstructor
public class AvatarConfigResponse {
    private Long id;
    private String avatarName;
    private String tone;
    private String style;
    private String voice;
}
