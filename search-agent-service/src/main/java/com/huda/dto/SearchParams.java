package com.huda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchParams(
        @JsonProperty("city") String city,
        @JsonProperty("genre") String genre,
        @JsonProperty("artist") String artist,
        @JsonProperty("maxPrice") Double maxPrice,
        @JsonProperty("date") String date
) {
    public String cacheKey() {
        return "cache:%s:%s:%s:%s:%s".formatted(
                city != null ? city.toLowerCase() : "any",
                genre != null ? genre.toLowerCase() : "any",
                artist != null ? artist.toLowerCase() : "any",
                maxPrice != null ? maxPrice : "any",
                date != null ? date : "any"
        );
    }
}
