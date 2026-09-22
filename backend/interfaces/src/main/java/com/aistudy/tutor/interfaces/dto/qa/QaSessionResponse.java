package com.aistudy.tutor.interfaces.dto.qa;

import com.aistudy.tutor.domain.qa.model.QaSessionStatus;
import com.aistudy.tutor.domain.qa.model.ReplyMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答疑会话响应
 */
@Data
@AllArgsConstructor
public class QaSessionResponse {
    private Long id;
    private String title;
    private Long courseId;
    private QaSessionStatus status;
    private ReplyMode replyMode;
    private LocalDateTime createdAt;
}
