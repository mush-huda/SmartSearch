package com.huda.tool;

import com.huda.dto.EventDto;
import com.huda.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventSearchTool {

    private final EventRepository eventRepository;

    @Tool(name = "search_events", description = "Search for live events by optional filters. All parameters are optional.")
    public List<EventDto> searchEvents(
            @ToolParam(description = "City where the event takes place", required = false) String city,
            @ToolParam(description = "Music genre, e.g. jazz, rock, classical", required = false) String genre,
            @ToolParam(description = "Artist or band name, e.g. Coldplay, Miles Davis", required = false) String artist,
            @ToolParam(description = "Maximum ticket price in EUR", required = false) Double maxPrice,
            @ToolParam(description = "Event date in ISO format yyyy-MM-dd", required = false) String date
    ) {
        log.info("Searching events with city={}, genre={}, artist={}, maxPrice={}, date={}",
                city, genre, artist, maxPrice, date);

        BigDecimal maxPriceDec = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

        return eventRepository.findByFilters(city, genre, artist, maxPriceDec, date)
                .stream()
                .map(EventDto::from)
                .peek(e -> log.info("Found event: {}", e))
                .toList();
    }
}
