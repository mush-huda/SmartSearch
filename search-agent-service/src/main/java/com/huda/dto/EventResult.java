package com.huda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EventResult(
        UUID id,
        String name,
        String artist,
        String city,
        String genre,
        String venue,
        LocalDate eventDate,
        BigDecimal priceEur,
        Integer availableSeats
) {}
