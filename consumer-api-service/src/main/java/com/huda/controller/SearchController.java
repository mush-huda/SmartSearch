package com.huda.controller;

import com.huda.dto.SearchResult;
import com.huda.dto.SearchRequest;
import com.huda.service.RateLimiterService;
import com.huda.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final RateLimiterService rateLimiter;
    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(
            @RequestHeader(value = "X-API-Key") String apiKey,
            @RequestBody SearchRequest request) {

        //Does spring handle it?
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Long> retryAfter = rateLimiter.checkLimit(apiKey);
        if (retryAfter.isPresent()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter.get()))
                    .build();
        }

        List<SearchResult> results = searchService.search(apiKey, request.getQuery());

        return ResponseEntity.ok(results);
    }
}
