package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.TaskItem;
import com.aistudy.tutor.domain.studyplan.repository.TaskItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 计划任务项仓储 Port 实现（findById 过滤软删除；今日待办仅查 TODO 状态）
 */
@Component
@RequiredArgsConstructor
public class TaskItemRepositoryImpl implements TaskItemRepository {

    private static final String STATUS_TODO = "TODO";

    private final TaskItemJpaRepository jpaRepository;

    @Override
    public TaskItem save(TaskItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public Optional<TaskItem> findById(Long id) {
        return jpaRepository.findById(id).filter(t -> !t.isDeleted());
    }

    @Override
    public List<TaskItem> findByScheduledDate(Long userId, LocalDate date) {
        return jpaRepository.findByUserIdAndScheduledDateAndStatusAndDeletedFalse(userId, date, STATUS_TODO);
    }

    @Override
    public List<TaskItem> findByNextReviewDate(Long userId, LocalDate date) {
        return jpaRepository.findByUserIdAndNextReviewDateAndStatusAndDeletedFalse(userId, date, STATUS_TODO);
    }
}
