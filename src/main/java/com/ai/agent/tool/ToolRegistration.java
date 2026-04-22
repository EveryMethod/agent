package com.ai.agent.tool;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author alh
 * @description 工具注册工厂
 * @date 2026/4/20 22:38
 */
@Configuration
public class ToolRegistration {

    @Value("${tool.search.api_key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
                new FileOperationTool(),
                new WebSearchTool(searchApiKey),
                new WebScrapingTool(),
                new ResourceDownloadTool(),
                new PDFGenerationTool(),
                new TerminalOperationTool(),
                new TerminateTool()
        );
    }
}
