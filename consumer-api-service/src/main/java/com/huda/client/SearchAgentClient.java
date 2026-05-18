package com.huda.client;

import com.huda.dto.SearchResult;
import com.huda.exception.SearchAgentClientException;
import com.huda.exception.SearchAgentServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

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
                    throw new SearchAgentClientException(res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new SearchAgentServerException(res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});
    }

}
