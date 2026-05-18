package com.huda.controller;

import com.huda.dto.EventResult;
import com.huda.dto.SearchRequest;
import com.huda.service.AgentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AgentController {

    private final AgentSearchService agentSearchService;

    @PostMapping("/agent/search")
    public ResponseEntity<List<EventResult>> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(agentSearchService.search(request.query()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
