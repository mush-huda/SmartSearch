package com.huda.service;

import com.huda.dto.SearchPerformedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${kafka.topics.consumer-api-service-log}")
    private String kafkaTopic;

    private final KafkaTemplate<String, SearchPerformedEvent> kafkaTemplate;

    @Async
    public void publishSearchEvent(String apiKey, String query) {
        log.info("Publishing search event for API Key: {}", apiKey);
        var event = SearchPerformedEvent.builder()
                .apiKey(apiKey)
                .rawQuery(query)
                .timestamp(Instant.now().toString())
                .build();
        kafkaTemplate.send(kafkaTopic, apiKey, event);
    }
}
