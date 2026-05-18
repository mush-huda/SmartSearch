package com.huda.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SearchResult {
    private String id;
    private String name;
    private String city;
    private String genre;
    private String venue;
    private String eventDate;
    private BigDecimal priceEur;
    private Integer availableSeats;
}
