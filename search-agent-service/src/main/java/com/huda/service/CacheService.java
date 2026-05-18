package com.huda.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huda.dto.EventResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.ttl-seconds}")
    private int ttlSeconds;

    public List<EventResult> get(String key) {
        String json = redis.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize cached value for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    public void set(String key, List<EventResult> results) {
        try {
            String json = objectMapper.writeValueAsString(results);
            redis.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.warn("Failed to cache results for key {}: {}", key, e.getMessage());
        }
    }
}
