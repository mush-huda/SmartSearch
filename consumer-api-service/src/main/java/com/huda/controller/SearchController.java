package com.huda.controller;

import com.huda.dto.SearchResult;
import com.huda.dto.SearchRequest;
import com.huda.exception.ApiKeyMissingException;
import com.huda.exception.RateLimitExceededException;
import com.huda.service.RateLimiterService;
import com.huda.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final RateLimiterService rateLimiter;
    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@RequestHeader(value = "X-API-Key") String apiKey,
                                                     @RequestBody SearchRequest request
    ) {
        log.warn("Received search request: {}", request.getQuery());

        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiKeyMissingException();
        }

        Optional<Long> retryAfter = rateLimiter.checkLimit(apiKey);
        if (retryAfter.isPresent()) {
            log.warn("API Key {} has reached the rate limit", apiKey);
            throw new RateLimitExceededException();
        }

        List<SearchResult> results = searchService.search(apiKey, request.getQuery());

        return ResponseEntity.ok(results);
    }
}
