package com.huda.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huda.dto.EventResult;
import com.huda.dto.SearchParams;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentSearchService {

    private final ChatClient chatClient;
    private final McpSyncClient mcpSyncClient;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public List<EventResult> search(String query) {
        // 1. Ask LLM to extract structured search params from the natural language query
        SearchParams params = extractParams(query);
        log.debug("Extracted params: {}", params);

        // 2. Check cache using the structured params as key (not the raw query)
        String cacheKey = params.cacheKey();
        List<EventResult> cached = cacheService.get(cacheKey);
        if (cached != null) {
            log.debug("Cache hit for key: {}", cacheKey);
            return cached;
        }

        // 3. Cache miss — call the MCP search_events tool
        log.debug("Cache miss for key: {}, calling MCP tool", cacheKey);
        List<EventResult> results = callSearchEventsTool(params);

        // 4. Store in cache for subsequent requests with the same semantic meaning
        cacheService.set(cacheKey, results);
        return results;
    }

    private SearchParams extractParams(String query) {
        return chatClient.prompt()
                .system("""
                        You are a search parameter extractor for a live events platform.
                        Extract search parameters from the user query and return ONLY a JSON object.
                        Fields (all optional, use null if not mentioned):
                          city: string — city name
                          genre: string — music genre (e.g. jazz, rock, classical, electronic)
                          artist: string — artist or band name (e.g. Coldplay, Miles Davis)
                          maxPrice: number — maximum ticket price in EUR
                          date: string — event date in ISO format yyyy-MM-dd
                        Return only the raw JSON object, no markdown, no explanation.
                        """)
                .user(query)
                .call()
                .entity(SearchParams.class);
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

        if (result.isError()) {
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
