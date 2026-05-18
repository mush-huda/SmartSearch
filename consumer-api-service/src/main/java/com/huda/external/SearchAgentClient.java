package com.huda.external;

import com.huda.dto.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class SearchAgentClient {

    private final RestClient restClient;

    public SearchAgentClient(@Value("${app.search-agent-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<SearchResult> search(String query) {
        return restClient.post()
                .uri("/agent/search")
                .body(Map.of("query", query))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new SearchAgentClientException(
                            format("search-agent-service failed with %s, body: %s", res.getStatusCode(), res.getBody()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new SearchAgentServerException(
                            format("search-agent-service failed with %s, body: %s", res.getStatusCode(), res.getBody()));
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public static class SearchAgentClientException extends RuntimeException {
        public SearchAgentClientException(String message) {
            super(message);
        }
    }

    public static class SearchAgentServerException extends RuntimeException {
        public SearchAgentServerException(String message) {
            super(message);
        }
    }

}
