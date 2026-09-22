package com.aistudy.tutor.infrastructure.persistence;

import com.aistudy.tutor.domain.classroom.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 课堂录音转写 JPA 仓储（Spring Data）
 */
@Repository
public interface TranscriptJpaRepository extends JpaRepository<Transcript, Long> {
}
