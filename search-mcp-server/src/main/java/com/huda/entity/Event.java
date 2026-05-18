package com.huda.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@Table(name = "events")
public class Event {

    @Id
    private UUID id;
    private String name;
    private String artist;
    private String city;
    private String genre;
    private String venue;
    private LocalDate eventDate;
    private BigDecimal priceEur;
    private Integer availableSeats;
}
