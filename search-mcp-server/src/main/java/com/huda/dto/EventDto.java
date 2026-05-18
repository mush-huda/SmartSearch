package com.huda.dto;

import com.huda.entity.Event;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EventDto {

    private UUID id;
    private String name;
    private String artist;
    private String city;
    private String genre;
    private String venue;
    private LocalDate eventDate;
    private BigDecimal priceEur;
    private Integer availableSeats;

    public static EventDto from(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .name(e.getName())
                .artist(e.getArtist())
                .city(e.getCity())
                .genre(e.getGenre())
                .venue(e.getVenue())
                .eventDate(e.getEventDate())
                .priceEur(e.getPriceEur())
                .availableSeats(e.getAvailableSeats())
                .build();
    }
}
