package com.aistudy.tutor.domain.studyplan.repository;

import com.aistudy.tutor.domain.studyplan.model.TaskItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 计划任务项仓储 Port（实现在 infrastructure.persistence）
 */
public interface TaskItemRepository {

    TaskItem save(TaskItem item);

    Optional<TaskItem> findById(Long id);

    /** 今日待办：scheduled_date = date 的 TODO 任务 */
    List<TaskItem> findByScheduledDate(Long userId, LocalDate date);

    /** 今日待办：next_review_date = date 的 TODO 任务 */
    List<TaskItem> findByNextReviewDate(Long userId, LocalDate date);
}
