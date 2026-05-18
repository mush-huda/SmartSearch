package com.huda.repository;

import com.huda.TestcontainersConfiguration;
import com.huda.entity.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Sql("/test-data/events.sql")
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventRepositoryIntegrationTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    void findByFilters_allNulls_returnsAllEvents() {
        List<Event> results = eventRepository.findByFilters(null, null, null, null, null);

        assertThat(results).hasSize(6);
    }

    @Test
    void findByFilters_byCity_returnsOnlyMatchingCity() {
        List<Event> results = eventRepository.findByFilters("Berlin", null, null, null, null);

        assertThat(results)
                .hasSize(3)
                .allMatch(e -> e.getCity().equals("Berlin"));
    }

    @Test
    void findByFilters_byCity_isCaseInsensitive() {
        List<Event> upper = eventRepository.findByFilters("BERLIN", null, null, null, null);
        List<Event> lower = eventRepository.findByFilters("berlin", null, null, null, null);

        assertThat(upper).hasSize(3);
        assertThat(lower).hasSize(3);
    }

    @Test
    void findByFilters_byGenre_returnsOnlyMatchingGenre() {
        List<Event> results = eventRepository.findByFilters(null, "jazz", null, null, null);

        assertThat(results)
                .hasSize(2)
                .allMatch(e -> e.getGenre().equals("jazz"));
    }

    @Test
    void findByFilters_byArtist_returnsOnlyMatchingArtist() {
        List<Event> results = eventRepository.findByFilters(null, null, "Coldplay", null, null);

        assertThat(results)
                .hasSize(2)
                .allMatch(e -> e.getArtist().equals("Coldplay"));
    }

    @Test
    void findByFilters_byArtist_isCaseInsensitive() {
        List<Event> results = eventRepository.findByFilters(null, null, "coldplay", null, null);

        assertThat(results)
                .hasSize(2)
                .allMatch(e -> e.getArtist().equals("Coldplay"));
    }

    @Test
    void findByFilters_byMaxPrice_returnsEventsAtOrBelowPrice() {
        List<Event> results = eventRepository.findByFilters(null, null, null, BigDecimal.valueOf(30), null);

        assertThat(results)
                .hasSize(3)
                .allMatch(e -> e.getPriceEur().compareTo(BigDecimal.valueOf(30)) <= 0);
    }

    @Test
    void findByFilters_byMaxPrice_includesExactMatch() {
        List<Event> results = eventRepository.findByFilters(null, null, null, BigDecimal.valueOf(25), null);

        assertThat(results).hasSize(2);
    }

    @Test
    void findByFilters_byDate_returnsOnlyMatchingDate() {
        List<Event> results = eventRepository.findByFilters(null, null, null, null, "2026-06-01");

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo("Berlin Jazz Nights");
    }

    @Test
    void findByFilters_byCityAndArtist_returnsIntersection() {
        List<Event> results = eventRepository.findByFilters("Berlin", null, "Coldplay", null, null);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo("Pop Extravaganza Berlin");
    }

    @Test
    void findByFilters_byCityAndMaxPrice_returnsMatchingEvents() {
        List<Event> results = eventRepository.findByFilters("Berlin", null, null, BigDecimal.valueOf(30), null);

        assertThat(results).hasSize(2); // 25.00 jazz and 15.00 electronic
    }

    @Test
    void findByFilters_noMatch_returnsEmptyList() {
        List<Event> results = eventRepository.findByFilters("Paris", null, null, null, null);

        assertThat(results).isEmpty();
    }
}
