package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.subject.model.ImportJob;
import com.aistudy.tutor.domain.subject.repository.ImportJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 导入任务仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class ImportJobRepositoryImpl implements ImportJobRepository {

    private final ImportJobJpaRepository jpaRepository;

    @Override
    public ImportJob save(ImportJob job) {
        return jpaRepository.save(job);
    }

    @Override
    public Optional<ImportJob> findById(Long id) {
        return jpaRepository.findById(id).filter(j -> !j.isDeleted());
    }
}
