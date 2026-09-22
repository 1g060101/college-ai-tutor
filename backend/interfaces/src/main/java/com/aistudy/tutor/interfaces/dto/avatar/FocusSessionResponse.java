package com.aistudy.tutor.interfaces.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专注计时响应
 */
@Data
@AllArgsConstructor
public class FocusSessionResponse {
    private Long id;
    private Long courseId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String status;
}
