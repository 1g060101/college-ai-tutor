package com.aistudy.tutor.domain.course.repository;

import com.aistudy.tutor.domain.course.model.Course;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;

import java.util.Optional;

/**
 * 课程仓储 Port（实现在 infrastructure.persistence）
 */
public interface CourseRepository {

    Course save(Course course);

    Optional<Course> findById(Long id);

    /** 课程列表分页（不含已删除） */
    PageResult<Course> page(PageQuery query);
}
