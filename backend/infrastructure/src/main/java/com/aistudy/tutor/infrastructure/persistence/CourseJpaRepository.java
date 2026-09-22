package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 课程 JPA 仓储（Spring Data）
 */
@Repository
public interface CourseJpaRepository extends JpaRepository<Course, Long> {

    /** 课程列表分页（不含已删除） */
    Page<Course> findByDeletedFalse(Pageable pageable);
}
