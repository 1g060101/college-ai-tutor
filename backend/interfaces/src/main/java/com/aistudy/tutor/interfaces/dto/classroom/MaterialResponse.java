package com.aistudy.tutor.interfaces.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 课件上传/解析响应
 */
@Data
@AllArgsConstructor
public class MaterialResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private String status;
    private String errorMsg;
    private String summary;
}
