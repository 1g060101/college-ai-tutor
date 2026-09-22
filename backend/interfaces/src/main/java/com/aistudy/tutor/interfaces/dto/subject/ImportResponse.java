package com.aistudy.tutor.interfaces.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 导入提交响应（返回任务 id 与初始状态，进度可轮询 /import-jobs/{id}）
 */
@Data
@AllArgsConstructor
public class ImportResponse {
    private Long jobId;
    private String status;
}
