package com.aistudy.tutor.interfaces.dto.avatar;

import lombok.Data;

/**
 * 创建专注计时请求
 */
@Data
public class FocusSessionRequest {

    /** 关联课程 id，可空 */
    private Long courseId;

    /** 预估专注时长（分钟），可空 */
    private Integer durationMinutes;
}
