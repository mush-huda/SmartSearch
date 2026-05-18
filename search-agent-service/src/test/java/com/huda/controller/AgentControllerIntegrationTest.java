package com.huda.controller;

import com.huda.TestcontainersConfiguration;
import com.huda.dto.EventResult;
import com.huda.dto.SearchParams;
import com.huda.external.AiService;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class AgentControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    StringRedisTemplate redis;

    @MockBean
    AiService aiService;
    @MockBean
    McpSyncClient mcpSyncClient;

    private static final SearchParams BERLIN_JAZZ = new SearchParams("Berlin", "jazz", null, 40.0, null);
    private static final String MCP_JSON = """
            [{"id":"a1000000-0000-0000-0000-000000000001","name":"Berlin Jazz Nights",
            "artist":"Miles Davis Tribute","city":"Berlin","genre":"jazz",
            "venue":"A-Trane Jazz Club","eventDate":"2026-06-01","priceEur":25.00,"availableSeats":120}]
            """;

    @BeforeEach
    void clearRedis() {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void searchReturnsMcpResultsOnCacheMiss() {
        when(aiService.extractParams(any())).thenReturn(BERLIN_JAZZ);
        when(mcpSyncClient.callTool(any())).thenReturn(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(MCP_JSON)), false));

        ResponseEntity<List<EventResult>> response = post("jazz in Berlin under 40");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().name()).isEqualTo("Berlin Jazz Nights");
        assertThat(response.getBody().getFirst().city()).isEqualTo("Berlin");
    }

    @Test
    void secondRequestHitsCacheAndSkipsMcp() {
        when(aiService.extractParams(any())).thenReturn(BERLIN_JAZZ);
        when(mcpSyncClient.callTool(any())).thenReturn(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(MCP_JSON)), false));

        post("jazz in Berlin under 40");
        post("jazz in Berlin under 40");

        verify(mcpSyncClient, times(1)).callTool(any());
    }

    @Test
    void cachedResultIsReturnedOnSecondRequest() {
        when(aiService.extractParams(any())).thenReturn(BERLIN_JAZZ);
        when(mcpSyncClient.callTool(any())).thenReturn(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(MCP_JSON)), false));

        ResponseEntity<List<EventResult>> first = post("jazz in Berlin under 40");
        ResponseEntity<List<EventResult>> second = post("jazz in Berlin under 40");

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getBody()).isEqualTo(first.getBody());
    }

    @Test
    void missingQueryBodyReturnsBadRequest() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/agent/search", new HttpEntity<>(Map.of()), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<List<EventResult>> post(String query) {
        return restTemplate.exchange(
                "/agent/search",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("query", query)),
                new ParameterizedTypeReference<>() {}
        );
    }
}
