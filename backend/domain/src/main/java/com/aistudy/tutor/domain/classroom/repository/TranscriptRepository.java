package com.aistudy.tutor.domain.classroom.repository;

import com.aistudy.tutor.domain.classroom.model.Transcript;

import java.util.Optional;

/**
 * 课堂录音转写仓储 Port（实现在 infrastructure.persistence）
 */
public interface TranscriptRepository {

    Transcript save(Transcript transcript);

    Optional<Transcript> findById(Long id);
}
