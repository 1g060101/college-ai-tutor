package com.aistudy.tutor.interfaces.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 导入任务状态响应
 */
@Data
@AllArgsConstructor
public class ImportJobResponse {
    private Long id;
    private String fileName;
    private String jobType;
    private String status;
    private int totalCount;
    private int successCount;
    private int failCount;
    private String errorMsg;
}
