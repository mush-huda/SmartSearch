package com.huda.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huda.dto.EventResult;
import com.huda.dto.SearchParams;
import com.huda.external.AiService;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentSearchService {

    private final AiService aiService;
    private final McpSyncClient mcpSyncClient;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public List<EventResult> search(String query) {
        SearchParams params = aiService.extractParams(query);
        if (params == null) {
            throw new IllegalArgumentException("Could not extract search parameters from query: " + query);
        }
        log.info("Extracted params: {}", params);

        String cacheKey = params.cacheKey();
        List<EventResult> cached = cacheService.get(cacheKey);
        if (cached != null) {
            log.info("Cache hit for key: {}", cacheKey);
            return cached;
        }

        log.info("Cache miss for key: {}, calling MCP tool", cacheKey);
        List<EventResult> results = callSearchEventsTool(params);

        cacheService.set(cacheKey, results);
        return results;
    }



    private List<EventResult> callSearchEventsTool(SearchParams params) {
        Map<String, Object> args = new HashMap<>();
        if (params.city() != null) args.put("city", params.city());
        if (params.genre() != null) args.put("genre", params.genre());
        if (params.artist() != null) args.put("artist", params.artist());
        if (params.maxPrice() != null) args.put("maxPrice", params.maxPrice());
        if (params.date() != null) args.put("date", params.date());

        McpSchema.CallToolResult result = mcpSyncClient.callTool(
                new McpSchema.CallToolRequest("search_events", args)
        );

        if (Boolean.TRUE.equals(result.isError())) {
            log.error("MCP tool call failed: {}", result.content());
            return List.of();
        }

        String json = ((McpSchema.TextContent) result.content().getFirst()).text();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize MCP tool result: {}", e.getMessage());
            return List.of();
        }
    }
}
