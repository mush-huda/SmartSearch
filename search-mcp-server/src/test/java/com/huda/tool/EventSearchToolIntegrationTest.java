package com.huda.tool;

import com.huda.TestcontainersConfiguration;
import com.huda.dto.EventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Sql("/test-data/events.sql")
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventSearchToolIntegrationTest {

    @Autowired
    EventSearchTool eventSearchTool;

    @Test
    void searchEvents_noFilters_returnsAllEvents() {
        List<EventDto> results = eventSearchTool.searchEvents(null, null, null, null, null);

        assertThat(results).hasSize(6);
    }

    @Test
    void searchEvents_byArtist_returnsMatchingEvents() {
        List<EventDto> results = eventSearchTool.searchEvents(null, null, "Coldplay", null, null);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(e -> e.getArtist().equals("Coldplay"));
    }

    @Test
    void searchEvents_byArtistAndCity_returnsIntersection() {
        List<EventDto> results = eventSearchTool.searchEvents("Berlin", null, "Coldplay", null, null);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getCity()).isEqualTo("Berlin");
        assertThat(results.getFirst().getArtist()).isEqualTo("Coldplay");
    }

    @Test
    void searchEvents_byMaxPrice_excludesExpensiveEvents() {
        List<EventDto> results = eventSearchTool.searchEvents(null, null, null, 30.0, null);

        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(e -> e.getPriceEur().doubleValue() <= 30.0);
    }

    @Test
    void searchEvents_artistIsCaseInsensitive() {
        List<EventDto> upper = eventSearchTool.searchEvents(null, null, "COLDPLAY", null, null);
        List<EventDto> lower = eventSearchTool.searchEvents(null, null, "coldplay", null, null);

        assertThat(upper).hasSize(2);
        assertThat(lower).hasSize(2);
    }

    @Test
    void searchEvents_returnsFullEventDetails() {
        List<EventDto> results = eventSearchTool.searchEvents("Hamburg", null, "Metallica", null, null);

        assertThat(results).hasSize(1);
        EventDto event = results.getFirst();
        assertThat(event.getName()).isEqualTo("Rock Am Hafen");
        assertThat(event.getVenue()).isEqualTo("Barclays Arena");
        assertThat(event.getGenre()).isEqualTo("rock");
        assertThat(event.getAvailableSeats()).isEqualTo(1500);
    }

    @Test
    void searchEvents_noMatch_returnsEmptyList() {
        List<EventDto> results = eventSearchTool.searchEvents("Paris", null, null, null, null);

        assertThat(results).isEmpty();
    }
}
