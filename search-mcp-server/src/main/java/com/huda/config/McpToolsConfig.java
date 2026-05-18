package com.huda.config;

import com.huda.tool.EventSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider eventSearchTools(EventSearchTool eventSearchTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(eventSearchTool)
                .build();
    }
}
