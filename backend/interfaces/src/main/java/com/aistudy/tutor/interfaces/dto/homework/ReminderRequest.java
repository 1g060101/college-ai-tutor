package com.aistudy.tutor.interfaces.dto.homework;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 截止/完成度提醒配置请求
 */
@Data
public class ReminderRequest {

    /** 是否开启提醒，默认开启 */
    private boolean enabled = true;

    /** 提前提醒小时数，默认 24 */
    @Min(value = 1, message = "提前提醒小时数至少为 1")
    @Max(value = 720, message = "提前提醒小时数最多为 720（30 天）")
    private int remindBeforeHours = 24;
}
