package com.aistudy.tutor.application.subject;

import com.aistudy.tutor.domain.course.model.Course;
import com.aistudy.tutor.domain.course.repository.CourseRepository;
import com.aistudy.tutor.domain.subject.model.FieldMapping;
import com.aistudy.tutor.domain.subject.model.ImportJob;
import com.aistudy.tutor.domain.subject.model.SubjectTemplate;
import com.aistudy.tutor.domain.subject.repository.FieldMappingRepository;
import com.aistudy.tutor.domain.subject.repository.ImportJobRepository;
import com.aistudy.tutor.domain.subject.repository.SubjectTemplateRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多学科/自定义课程导入应用服务：学科模板配置 / 课程批量导入 / 导入任务状态查询
 */
@Service
@RequiredArgsConstructor
public class ImportAppService {

    private final SubjectTemplateRepository subjectTemplateRepository;
    private final FieldMappingRepository fieldMappingRepository;
    private final ImportJobRepository importJobRepository;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    /**
     * 配置学科模板：保存 subject_template（fields 序列化为 JSON）+ 对应 field_mapping 行，返回模板 id
     */
    @Transactional
    public Long createTemplate(String subject, String templateName, List<FieldMapping> mappings) {
        List<Map<String, String>> fieldList = new ArrayList<>();
        for (FieldMapping mapping : mappings) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("sourceField", mapping.getSourceField());
            item.put("targetField", mapping.getTargetField());
            fieldList.add(item);
        }
        SubjectTemplate template = subjectTemplateRepository.save(
                new SubjectTemplate(subject, templateName, writeJson(fieldList)));
        for (FieldMapping mapping : mappings) {
            mapping.bindTemplate(template.getId());
            fieldMappingRepository.save(mapping);
        }
        return template.getId();
    }

    /**
     * 自定义课程导入：创建 RUNNING 任务 → 逐行解析（课程名|学科|描述|难度，支持 Markdown 表格行）
     * 成功逐条保存 Course，失败累计 fail_count 并记录首条错误；全部处理完置为 DONE。
     * content 与文件均为空 → FAILED（error_msg=无内容可导入）。
     */
    @Transactional
    public ImportJob importCourses(Long userId, Long templateId, String fileName, String content) {
        subjectTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(404, "学科模板不存在"));

        ImportJob job = importJobRepository.save(new ImportJob(userId,
                fileName == null || fileName.isBlank() ? "未命名" : fileName, "COURSE", "RUNNING"));
        if (content == null || content.isBlank()) {
            job.markFailed("无内容可导入");
            return importJobRepository.save(job);
        }

        List<String> lines = parseLines(content);
        int success = 0;
        int fail = 0;
        String firstError = null;
        for (String line : lines) {
            try {
                courseRepository.save(parseCourse(line));
                success++;
            } catch (Exception e) {
                fail++;
                if (firstError == null) {
                    firstError = e.getMessage();
                }
            }
        }
        job.markDone(lines.size(), success, fail);
        job.recordFirstError(firstError);
        return importJobRepository.save(job);
    }

    /**
     * 导入任务状态：校验归属当前用户，否则 403
     */
    @Transactional(readOnly = true)
    public ImportJob getImportJob(Long userId, Long jobId) {
        ImportJob job = importJobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(404, "导入任务不存在"));
        if (!job.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该导入任务");
        }
        return job;
    }

    /**
     * 按行拆分内容，跳过空行、Markdown 表头行与分隔行
     */
    private List<String> parseLines(String content) {
        List<String> lines = new ArrayList<>();
        for (String raw : content.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.isBlank()) {
                continue;
            }
            // 跳过 Markdown 表头行（含 课程名/学科 列名）
            if (line.contains("课程名") && line.contains("学科")) {
                continue;
            }
            // 跳过 Markdown 分隔行（如 |---|---|、:---: 等）
            String stripped = line.replace("|", "").replace(":", "").replace("-", "").replace(" ", "");
            if (!stripped.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * 解析单行课程：格式「课程名|学科|描述|难度(1-5)」，描述/难度可缺省，难度缺省 3
     */
    private Course parseCourse(String line) {
        String[] parts = line.split("\\|", -1);
        int start = 0;
        int end = parts.length;
        // 去掉首尾空白段（Markdown 表格行首尾带 |）
        while (start < end && parts[start].isBlank()) {
            start++;
        }
        while (end > start && parts[end - 1].isBlank()) {
            end--;
        }
        if (end - start < 2) {
            throw new BusinessException(400, "行格式错误：至少需要「课程名|学科」");
        }
        String name = parts[start].trim();
        String subject = parts[start + 1].trim();
        if (name.isBlank() || subject.isBlank()) {
            throw new BusinessException(400, "课程名与学科不能为空");
        }
        String description = end - start > 2 && !parts[start + 2].isBlank() ? parts[start + 2].trim() : null;
        int difficulty = 3;
        if (end - start > 3 && !parts[start + 3].isBlank()) {
            try {
                difficulty = Integer.parseInt(parts[start + 3].trim());
            } catch (NumberFormatException e) {
                throw new BusinessException(400, "难度必须为 1-5 的数字");
            }
            if (difficulty < 1 || difficulty > 5) {
                throw new BusinessException(400, "难度必须为 1-5");
            }
        }
        return new Course(name, subject, description, null, difficulty);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(500, "模板字段序列化失败");
        }
    }
}
