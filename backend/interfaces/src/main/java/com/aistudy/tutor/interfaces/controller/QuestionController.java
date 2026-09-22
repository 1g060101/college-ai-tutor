package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.question.QuestionAppService;
import com.aistudy.tutor.interfaces.dto.report.SimilarQuestionResponse;
import com.aistudy.tutor.interfaces.dto.report.SubmitAnswerRequest;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 题目接口：提交作答 / 同类题推荐。
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionAppService questionAppService;

    /** 提交作答 */
    @PostMapping("/{id}/answers")
    public Result<String> submitAnswer(@PathVariable Long id, @Valid @RequestBody SubmitAnswerRequest request) {
        questionAppService.submitAnswer(SecurityUtils.currentUserId(), id, request.getAnswerContent(),
                request.isCorrect(), request.getScore(), request.getDurationSeconds(), request.getRequestId());
        return Result.success("作答已记录");
    }

    /** 同类题推荐 */
    @GetMapping("/{id}/similar")
    public Result<List<SimilarQuestionResponse>> similar(@PathVariable Long id) {
        List<Map<String, Object>> list = questionAppService.similarQuestions(SecurityUtils.currentUserId(), id);
        List<SimilarQuestionResponse> responses = list.stream().map(this::toResponse).toList();
        return Result.success(responses);
    }

    private SimilarQuestionResponse toResponse(Map<String, Object> m) {
        return new SimilarQuestionResponse(
                (Long) m.get("questionId"),
                (String) m.get("content"),
                (String) m.get("type"),
                (Integer) m.get("difficulty"),
                (BigDecimal) m.get("masteryScore"),
                (String) m.get("reasoning"));
    }
}
