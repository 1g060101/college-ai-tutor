package com.aistudy.tutor.domain.qa.repository;

import com.aistudy.tutor.domain.qa.model.QaTurn;

import java.util.List;

/**
 * 答疑单轮对话仓储 Port（实现在 infrastructure.persistence）
 */
public interface QaTurnRepository {

    QaTurn save(QaTurn turn);

    List<QaTurn> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
