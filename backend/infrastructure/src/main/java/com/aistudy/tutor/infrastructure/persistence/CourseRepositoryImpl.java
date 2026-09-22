package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.course.model.Course;
import com.aistudy.tutor.domain.course.repository.CourseRepository;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 课程仓储 Port 实现（findById 过滤软删除，分页用 Spring Data Page）
 */
@Component
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final CourseJpaRepository jpaRepository;

    @Override
    public Course save(Course course) {
        return jpaRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return jpaRepository.findById(id).filter(c -> !c.isDeleted());
    }

    @Override
    public PageResult<Course> page(PageQuery query) {
        Page<Course> page = jpaRepository.findByDeletedFalse(PageRequest.of(query.pageNum() - 1, query.pageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements(),
                query.pageNum(), query.pageSize(), PageQuery.totalPages(page.getTotalElements(), query.pageSize()));
    }
}
