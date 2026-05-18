package com.huda.repository;

import com.huda.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = """
            SELECT * FROM events
            WHERE (CAST(:city AS varchar) IS NULL OR LOWER(city) = LOWER(CAST(:city AS varchar)))
            AND (CAST(:genre AS varchar) IS NULL OR LOWER(genre) = LOWER(CAST(:genre AS varchar)))
            AND (CAST(:artist AS varchar) IS NULL OR LOWER(artist) = LOWER(CAST(:artist AS varchar)))
            AND (CAST(:maxPrice AS numeric) IS NULL OR price_eur <= CAST(:maxPrice AS numeric))
            AND (CAST(:date AS date) IS NULL OR event_date = CAST(:date AS date))
            """, nativeQuery = true)
    List<Event> findByFilters(
            @Param("city") String city,
            @Param("genre") String genre,
            @Param("artist") String artist,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("date") String date
    );
}
