package com.aistudy.tutor.infrastructure.aigateway;

import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI 调用限流 — 单用户并发上限（默认 3 路）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiRateLimiter {

    private final ConcurrentHashMap<Long, AtomicInteger> inflight = new ConcurrentHashMap<>();

    @Value("${app.ai.max-concurrency-per-user:3}")
    private int maxConcurrency;

    /**
     * 尝试获取一个并发名额，超出则抛业务异常
     */
    public void acquire(Long userId) {
        if (userId == null) return;
        AtomicInteger counter = inflight.computeIfAbsent(userId, k -> new AtomicInteger(0));
        int now = counter.incrementAndGet();
        if (now > maxConcurrency) {
            counter.decrementAndGet();
            throw new BusinessException(429, "AI 请求过于频繁，请稍后再试");
        }
    }

    /**
     * 释放并发名额
     */
    public void release(Long userId) {
        if (userId == null) return;
        AtomicInteger counter = inflight.get(userId);
        if (counter != null) {
            counter.decrementAndGet();
            if (counter.get() <= 0) {
                inflight.remove(userId);
            }
        }
    }
}