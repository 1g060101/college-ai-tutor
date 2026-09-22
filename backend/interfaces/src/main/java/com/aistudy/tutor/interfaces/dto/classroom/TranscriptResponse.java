package com.aistudy.tutor.interfaces.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 录音转写响应
 */
@Data
@AllArgsConstructor
public class TranscriptResponse {
    private Long id;
    private String lectureName;
    private String summary;
}
