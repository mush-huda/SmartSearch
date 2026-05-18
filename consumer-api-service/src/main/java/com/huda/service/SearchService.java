package com.huda.service;

import com.huda.client.SearchAgentClient;
import com.huda.dto.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchAgentClient searchAgentClient;
    private final KafkaProducerService kafkaProducer;

    public List<SearchResult> search(String apiKey, String query) {
        List<SearchResult> results = searchAgentClient.search(query);
        kafkaProducer.publishSearchEvent(apiKey, query);
        return results;
    }
}
