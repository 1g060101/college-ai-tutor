package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.subject.ImportAppService;
import com.aistudy.tutor.domain.subject.model.FieldMapping;
import com.aistudy.tutor.domain.subject.model.ImportJob;
import com.aistudy.tutor.interfaces.dto.subject.ImportJobResponse;
import com.aistudy.tutor.interfaces.dto.subject.ImportResponse;
import com.aistudy.tutor.interfaces.dto.subject.SubjectTemplateRequest;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 多学科/自定义课程导入接口：配置学科模板 / 课程导入（文件或文本）/ 导入任务状态
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubjectImportController {

    private final ImportAppService importAppService;

    /**
     * 配置学科模板：返回模板 id
     */
    @PostMapping("/subjects")
    public Result<Long> createTemplate(@Valid @RequestBody SubjectTemplateRequest request) {
        List<FieldMapping> mappings = request.getFields() == null ? List.of() : request.getFields().stream()
                .map(f -> new FieldMapping(f.getSourceField(), f.getTargetField()))
                .toList();
        Long templateId = importAppService.createTemplate(request.getSubject(), request.getTemplateName(), mappings);
        return Result.success(templateId);
    }

    /**
     * 自定义课程导入：file 上传或 content 文本二选一（支持 Markdown/CSV 两种格式），
     * 每行一条课程，字段用 | 分隔：课程名|学科|描述|难度(1-5)
     */
    @PostMapping("/subjects/{id}/import")
    public Result<ImportResponse> importCourses(@PathVariable Long id,
                                                @RequestParam(value = "file", required = false) MultipartFile file,
                                                @RequestParam(value = "content", required = false) String content) throws IOException {
        Long userId = SecurityUtils.currentUserId();
        String fileName = null;
        String text = content;
        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            text = new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        ImportJob job = importAppService.importCourses(userId, id, fileName, text);
        return Result.success(new ImportResponse(job.getId(), job.getStatus()));
    }

    /**
     * 导入任务状态（校验归属当前用户，否则 403）
     */
    @GetMapping("/import-jobs/{id}")
    public Result<ImportJobResponse> importJob(@PathVariable Long id) {
        ImportJob job = importAppService.getImportJob(SecurityUtils.currentUserId(), id);
        return Result.success(new ImportJobResponse(job.getId(), job.getFileName(), job.getJobType(),
                job.getStatus(), job.getTotalCount(), job.getSuccessCount(), job.getFailCount(), job.getErrorMsg()));
    }
}
