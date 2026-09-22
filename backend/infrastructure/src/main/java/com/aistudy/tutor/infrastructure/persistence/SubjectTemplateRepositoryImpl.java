package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.SubjectTemplate;
import com.aistudy.tutor.domain.subject.repository.SubjectTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 学科模板仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class SubjectTemplateRepositoryImpl implements SubjectTemplateRepository {

    private final SubjectTemplateJpaRepository jpaRepository;

    @Override
    public SubjectTemplate save(SubjectTemplate template) {
        return jpaRepository.save(template);
    }

    @Override
    public Optional<SubjectTemplate> findById(Long id) {
        return jpaRepository.findById(id).filter(t -> !t.isDeleted());
    }
}
