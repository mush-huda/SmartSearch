package com.huda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redis;
    private final int windowSeconds;
    private final int maxRequests;

    public RateLimiterService(StringRedisTemplate redis,
                              @Value("${app.rate-limit.window-seconds}") int windowSeconds,
                              @Value("${app.rate-limit.max-requests}") int maxRequests) {
        this.redis = redis;
        this.windowSeconds = windowSeconds;
        this.maxRequests = maxRequests;
    }

    /**
     * Returns empty if the request is allowed, or the retry-after seconds if rate limited.
     */
    public Optional<Long> checkLimit(String apiKey) {
        String key = "rate_limit:" + apiKey;
        long nowMs = Instant.now().toEpochMilli();
        long windowMs = (long) windowSeconds * 1000;

        redis.opsForZSet().removeRangeByScore(key, 0, nowMs - windowMs);

        Long count = redis.opsForZSet().zCard(key);
        if (count != null && count >= maxRequests) {
            // Find the oldest entry to compute how long until the window slides
            var oldest = redis.opsForZSet().rangeWithScores(key, 0, 0);
            long retryAfter = windowSeconds;
            if (oldest != null && !oldest.isEmpty()) {
                double oldestScore = oldest.iterator().next().getScore();
                long msUntilExpiry = (long) oldestScore + windowMs - nowMs;
                retryAfter = Math.max(1, (msUntilExpiry + 999) / 1000);
            }
            return Optional.of(retryAfter);
        }

        redis.opsForZSet().add(key, String.valueOf(nowMs), nowMs);
        redis.expire(key, Duration.ofSeconds(windowSeconds));
        return Optional.empty();
    }
}
