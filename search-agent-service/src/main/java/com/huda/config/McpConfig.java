package com.huda.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Value("${spring.ai.mcp.client.url}")
    private String mcpServerUrl;

    @Bean
    @ConditionalOnMissingBean
    public McpSyncClient mcpSyncClient() {
        McpSyncClient mcpClient = McpClient
                .sync(HttpClientSseClientTransport.builder(mcpServerUrl).build())
                .build();
        mcpClient.initialize();
        return mcpClient;
    }
}
