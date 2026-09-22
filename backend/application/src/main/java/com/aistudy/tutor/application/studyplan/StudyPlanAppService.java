package com.aistudy.tutor.application.studyplan;

import com.aistudy.tutor.application.report.MasteryRecalcService;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.report.service.EbbinghausService;
import com.aistudy.tutor.domain.studyplan.model.StudyPlan;
import com.aistudy.tutor.domain.studyplan.model.StudyRecord;
import com.aistudy.tutor.domain.studyplan.model.TaskItem;
import com.aistudy.tutor.domain.studyplan.repository.StudyPlanRepository;
import com.aistudy.tutor.domain.studyplan.repository.StudyRecordRepository;
import com.aistudy.tutor.domain.studyplan.repository.TaskItemRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 预习复习规划应用服务：生成计划 / 今日回顾 / 完成任务 / 抽背判分。
 */
@Service
@RequiredArgsConstructor
public class StudyPlanAppService {

    private static final String TASK_PREVIEW = "PREVIEW";
    private static final String TASK_REVIEW = "REVIEW";

    /** 艾宾浩斯记忆曲线：纯领域服务（无 Spring 注解），直接持有实例 */
    private static final EbbinghausService EBBINGHAUS = new EbbinghausService();

    private final StudyPlanRepository studyPlanRepository;
    private final TaskItemRepository taskItemRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final MasteryRecalcService masteryRecalcService;

    /** 生成计划结果：计划实体 + 生成的任务数 */
    public record PlanCreateResult(StudyPlan plan, int taskCount) {}

    /**
     * 生成计划：为课程每个知识点生成一个 PREVIEW 任务（scheduled_date 从 startDate 起按序每天一个）
     * 与一个 REVIEW 任务（艾宾浩斯初始阶段 0、答对得首次复习日期，阶段值存 repeat_interval）。
     * 学科暂无可取来源（Course 领域模型未提供），传 null 使用默认系数 1.0。
     */
    @Transactional
    public PlanCreateResult createPlan(Long userId, Long courseId, String title, String period,
                                       LocalDate startDate, LocalDate endDate) {
        StudyPlan plan = studyPlanRepository.save(
                new StudyPlan(userId, courseId, title, period == null ? "WEEK" : period, startDate, endDate));
        List<KnowledgePoint> kps = knowledgePointRepository.findAllActive().stream()
                .filter(kp -> courseId == null || courseId.equals(kp.getCourseId()))
                .toList();
        for (int i = 0; i < kps.size(); i++) {
            KnowledgePoint kp = kps.get(i);
            // 预习任务：每天一个，按知识点顺序排布
            taskItemRepository.save(new TaskItem(plan.getId(), userId, kp.getId(),
                    "预习：" + kp.getName(), TASK_PREVIEW, startDate.plusDays(i)));
            // 复习任务：艾宾浩斯初始阶段 0、correct=true，firstReviewDate = startDate + 间隔
            EbbinghausService.EbbinghausResult result = EBBINGHAUS.schedule(0, true, null, startDate);
            TaskItem reviewTask = new TaskItem(plan.getId(), userId, kp.getId(),
                    "复习：" + kp.getName(), TASK_REVIEW, null);
            reviewTask.advanceReview(result.currentStage(), result.repeatIntervalDays(), result.nextReviewDate());
            taskItemRepository.save(reviewTask);
        }
        return new PlanCreateResult(plan, kps.size() * 2);
    }

    /**
     * 今日回顾：scheduled_date = today 或 next_review_date = today 的 TODO 任务（按 id 去重）
     */
    @Transactional(readOnly = true)
    public List<TaskItem> todayTasks(Long userId, LocalDate today) {
        Set<Long> seen = new HashSet<>();
        List<TaskItem> tasks = new ArrayList<>();
        for (TaskItem item : taskItemRepository.findByScheduledDate(userId, today)) {
            if (seen.add(item.getId())) {
                tasks.add(item);
            }
        }
        for (TaskItem item : taskItemRepository.findByNextReviewDate(userId, today)) {
            if (seen.add(item.getId())) {
                tasks.add(item);
            }
        }
        return tasks;
    }

    /**
     * 完成任务项：置 DONE，艾宾浩斯推进阶段与 next_review_date，写 REVIEW 学习记录；
     * 答对且命中知识点时回写掌握度。返回推进后的任务项。
     */
    @Transactional
    public TaskItem completeTask(Long userId, Long planId, Long taskId, boolean correct, Integer durationSeconds) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(404, "学习计划不存在"));
        if (!plan.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该学习计划");
        }
        TaskItem task = taskItemRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(404, "任务项不存在"));
        if (!task.getPlanId().equals(planId) || !task.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该任务项");
        }
        int currentStage = task.getRepeatInterval() == null ? 0 : task.getRepeatInterval();
        EbbinghausService.EbbinghausResult result = EBBINGHAUS.schedule(currentStage, correct, null, LocalDate.now());
        task.markDone();
        task.advanceReview(result.currentStage(), result.repeatIntervalDays(), result.nextReviewDate());
        taskItemRepository.save(task);

        studyRecordRepository.save(new StudyRecord(userId, plan.getCourseId(), task.getKnowledgePointId(),
                durationSeconds == null ? 0 : durationSeconds, "REVIEW", LocalDate.now()));
        if (correct && task.getKnowledgePointId() != null) {
            masteryRecalcService.recordEvent(userId, plan.getCourseId(), task.getKnowledgePointId(),
                    true, null, "STUDY", UUID.randomUUID().toString());
        }
        return task;
    }

    /** 抽背/默写/公式速记判分结果 */
    public record ReciteResult(boolean correct, int score, String matchedContent) {}

    /**
     * 抽背/默写/公式速记判分：归一化（去空白、全小写）后相等或互相包含即判对；
     * 写 STUDY 学习记录；kpId 非空时回写掌握度。matchedContent 为归一化后的作答内容。
     */
    @Transactional
    public ReciteResult recite(Long userId, Long kpId, Long courseId, String content, String expected) {
        String normalizedContent = normalize(content);
        String normalizedExpected = normalize(expected);
        boolean correct = normalizedContent.equals(normalizedExpected)
                || normalizedContent.contains(normalizedExpected)
                || normalizedExpected.contains(normalizedContent);
        studyRecordRepository.save(new StudyRecord(userId, courseId, kpId, 0, "STUDY", LocalDate.now()));
        if (kpId != null) {
            masteryRecalcService.recordEvent(userId, courseId, kpId, correct, null, "STUDY",
                    UUID.randomUUID().toString());
        }
        return new ReciteResult(correct, correct ? 100 : 0, normalizedContent);
    }

    private String normalize(String text) {
        return text == null ? "" : text.replaceAll("\\s+", "").toLowerCase();
    }
}
