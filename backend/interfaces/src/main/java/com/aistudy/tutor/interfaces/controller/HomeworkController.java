package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.question.HomeworkAppService;
import com.aistudy.tutor.interfaces.dto.homework.CreateAssignmentRequest;
import com.aistudy.tutor.interfaces.dto.homework.ErrorBookResponse;
import com.aistudy.tutor.interfaces.dto.homework.ReminderRequest;
import com.aistudy.tutor.interfaces.dto.homework.SubmitAssignmentRequest;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;
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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业与习题辅导接口：作业创建 / 作答提交（自动收录错题本）/ 错题本查询 / 提醒配置。
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkAppService homeworkAppService;

    /**
     * 上传/创建作业：批量创建题目，返回题目 id 列表 + 数量
     */
    @PostMapping("/assignments")
    public Result<Map<String, Object>> createAssignments(@Valid @RequestBody CreateAssignmentRequest request) {
        List<HomeworkAppService.QuestionInput> inputs = request.getQuestions().stream()
                .map(q -> new HomeworkAppService.QuestionInput(q.getContent(), q.getType(), q.getAnswer(),
                        q.getAnalysis(), q.getDifficulty(), q.getKnowledgePointId()))
                .toList();
        List<Long> ids = homeworkAppService.createAssignments(request.getCourseId(), inputs);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("questionIds", ids);
        result.put("count", ids.size());
        return Result.success(result);
    }

    /**
     * 提交作业作答：答错自动收录错题本（同一题目 upsert）
     */
    @PostMapping("/assignments/{id}/answers")
    public Result<String> submitAnswer(@PathVariable Long id, @Valid @RequestBody SubmitAssignmentRequest request) {
        Long userId = SecurityUtils.currentUserId();
        String message = homeworkAppService.submitAnswer(userId, id, request.getAnswerContent(),
                request.isCorrect(), request.getScore(), request.getDurationSeconds(), request.getRequestId());
        return Result.success(message);
    }

    /**
     * 错题本分页查询（可按知识点过滤）
     */
    @GetMapping("/error-book")
    public Result<PageResult<ErrorBookResponse>> errorBook(@RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(required = false) Long knowledgePointId) {
        Long userId = SecurityUtils.currentUserId();
        PageResult<Map<String, Object>> page = homeworkAppService.pageErrorBook(userId, knowledgePointId, new PageQuery(pageNum, pageSize));
        List<ErrorBookResponse> list = page.getList().stream().map(this::toErrorBookResponse).toList();
        return Result.success(new PageResult<>(list, page.getTotal(), page.getPageNum(), page.getPageSize(), page.getTotalPages()));
    }

    /**
     * 保存截止/完成度提醒配置（按用户 upsert）
     */
    @PostMapping("/reminders")
    public Result<String> saveReminder(@Valid @RequestBody ReminderRequest request) {
        Long userId = SecurityUtils.currentUserId();
        homeworkAppService.saveReminderConfig(userId, request.isEnabled(), request.getRemindBeforeHours());
        return Result.success("提醒配置已保存");
    }

    private ErrorBookResponse toErrorBookResponse(Map<String, Object> m) {
        return new ErrorBookResponse(
                (Long) m.get("id"),
                (Long) m.get("questionId"),
                (String) m.get("content"),
                (Long) m.get("knowledgePointId"),
                (String) m.get("kpName"),
                (String) m.get("wrongAnswer"),
                (String) m.get("errorReason"),
                (Integer) m.get("errorCount"),
                (Boolean) m.get("mastered"),
                (LocalDateTime) m.get("lastWrongAt"));
    }
}
