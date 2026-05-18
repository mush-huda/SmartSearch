package com.huda.external;

import com.huda.dto.SearchParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ClassPathResource promptResource = new ClassPathResource("prompts/extract-params.st");

    public SearchParams extractParams(String query) {
        return chatClient.prompt()
                .system(promptResource)
                .user(query)
                .call()
                .entity(SearchParams.class);
    }
}
