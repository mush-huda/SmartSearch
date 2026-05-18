package com.huda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Structured parameters extracted from a natural language query by the LLM.
 * All fields are nullable — the LLM only populates what the user mentioned.
 */
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
