package com.huda.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Value("${spring.ai.mcp.client.url}")
    private String mcpServerUrl;

    @Bean
    public McpSyncClient mcpSyncClient() {
        var transport = new HttpClientSseClientTransport.Builder(mcpServerUrl).build();
        McpSyncClient mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();
        return mcpClient;
    }
}
