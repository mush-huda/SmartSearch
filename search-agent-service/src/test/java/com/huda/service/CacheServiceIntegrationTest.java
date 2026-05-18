package com.huda.service;

import com.huda.TestcontainersConfiguration;
import com.huda.dto.EventResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CacheServiceIntegrationTest {

    @Autowired
    CacheService cacheService;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void clearRedis() {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertThat(cacheService.get("nonexistent")).isNull();
    }

    @Test
    void setThenGetReturnsStoredResults() {
        var results = sampleResults();

        cacheService.set("cache:berlin:jazz:any:any:any", results);

        assertThat(cacheService.get("cache:berlin:jazz:any:any:any"))
                .hasSize(1)
                .first()
                .satisfies(r -> {
                    assertThat(r.name()).isEqualTo("Berlin Jazz Nights");
                    assertThat(r.city()).isEqualTo("Berlin");
                    assertThat(r.priceEur()).isEqualByComparingTo("25.00");
                });
    }

    @Test
    void setEmptyListIsCachedAndReturned() {
        cacheService.set("cache:any:any:any:any:any", List.of());
        assertThat(cacheService.get("cache:any:any:any:any:any")).isEmpty();
    }

    @Test
    void differentKeysAreIndependent() {
        cacheService.set("cache:berlin:jazz:any:any:any", sampleResults());
        cacheService.set("cache:hamburg:rock:any:any:any", List.of());

        assertThat(cacheService.get("cache:berlin:jazz:any:any:any")).hasSize(1);
        assertThat(cacheService.get("cache:hamburg:rock:any:any:any")).isEmpty();
    }

    @Test
    void corruptedCacheValueReturnsNull() {
        redis.opsForValue().set("cache:bad:key", "not valid json {{{");
        assertThat(cacheService.get("cache:bad:key")).isNull();
    }

    private List<EventResult> sampleResults() {
        return List.of(new EventResult(
                UUID.fromString("a1000000-0000-0000-0000-000000000001"),
                "Berlin Jazz Nights",
                "Miles Davis Tribute",
                "Berlin",
                "jazz",
                "A-Trane Jazz Club",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("25.00"),
                120
        ));
    }
}
