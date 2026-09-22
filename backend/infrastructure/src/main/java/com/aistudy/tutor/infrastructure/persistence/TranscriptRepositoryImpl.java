package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Transcript;
import com.aistudy.tutor.domain.classroom.repository.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 课堂录音转写仓储 Port 实现（findById 过滤软删除）
 */
@Component
@RequiredArgsConstructor
public class TranscriptRepositoryImpl implements TranscriptRepository {

    private final TranscriptJpaRepository jpaRepository;

    @Override
    public Transcript save(Transcript transcript) {
        return jpaRepository.save(transcript);
    }

    @Override
    public Optional<Transcript> findById(Long id) {
        return jpaRepository.findById(id).filter(t -> !t.isDeleted());
    }
}
