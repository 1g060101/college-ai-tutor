package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.studyplan.StudyPlanAppService;
import com.aistudy.tutor.domain.studyplan.model.TaskItem;
import com.aistudy.tutor.interfaces.dto.studyplan.CompleteTaskRequest;
import com.aistudy.tutor.interfaces.dto.studyplan.CreatePlanRequest;
import com.aistudy.tutor.interfaces.dto.studyplan.CreatePlanResponse;
import com.aistudy.tutor.interfaces.dto.studyplan.ReciteRequest;
import com.aistudy.tutor.interfaces.dto.studyplan.ReciteResponse;
import com.aistudy.tutor.interfaces.dto.studyplan.TodayTaskResponse;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预习复习规划接口：生成计划 / 今日回顾 / 完成任务项 / 抽背判分。
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanAppService studyPlanAppService;

    /** 生成预习复习计划 */
    @PostMapping("/study-plans")
    public Result<CreatePlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        StudyPlanAppService.PlanCreateResult result = studyPlanAppService.createPlan(SecurityUtils.currentUserId(),
                request.getCourseId(), request.getTitle(), request.getPeriod(), request.getStartDate(), request.getEndDate());
        return Result.success(new CreatePlanResponse(result.plan().getId(), result.taskCount()));
    }

    /** 今日回顾：scheduled_date=今天 或 next_review_date=今天的 TODO 任务 */
    @GetMapping("/study-plans/today")
    public Result<List<TodayTaskResponse>> today() {
        List<TaskItem> tasks = studyPlanAppService.todayTasks(SecurityUtils.currentUserId(), LocalDate.now());
        return Result.success(tasks.stream().map(t -> new TodayTaskResponse(
                t.getPlanId(), t.getId(), t.getTitle(), t.getTaskType(), t.getKnowledgePointId(), t.getNextReviewDate()
        )).toList());
    }

    /** 完成任务项：艾宾浩斯推进复习，返回推进后的 nextReviewDate */
    @PostMapping("/study-plans/{id}/sessions")
    public Result<Map<String, LocalDate>> completeTask(@PathVariable Long id,
                                                       @Valid @RequestBody CompleteTaskRequest request) {
        TaskItem task = studyPlanAppService.completeTask(SecurityUtils.currentUserId(), id,
                request.getTaskId(), request.isCorrect(), request.getDurationSeconds());
        return Result.success(Map.of("nextReviewDate", task.getNextReviewDate()));
    }

    /** 抽背/默写/公式速记判分 */
    @PostMapping("/quizzes/recite")
    public Result<ReciteResponse> recite(@Valid @RequestBody ReciteRequest request) {
        StudyPlanAppService.ReciteResult result = studyPlanAppService.recite(SecurityUtils.currentUserId(),
                request.getKpId(), request.getCourseId(), request.getContent(), request.getExpected());
        return Result.success(new ReciteResponse(result.correct(), result.score(), result.matchedContent()));
    }
}
