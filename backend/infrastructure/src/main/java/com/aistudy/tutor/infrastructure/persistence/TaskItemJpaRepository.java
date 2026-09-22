package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.studyplan.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 计划任务项 JPA 仓储（Spring Data）
 */
@Repository
public interface TaskItemJpaRepository extends JpaRepository<TaskItem, Long> {

    List<TaskItem> findByUserIdAndScheduledDateAndStatusAndDeletedFalse(Long userId, LocalDate date, String status);

    List<TaskItem> findByUserIdAndNextReviewDateAndStatusAndDeletedFalse(Long userId, LocalDate date, String status);
}
