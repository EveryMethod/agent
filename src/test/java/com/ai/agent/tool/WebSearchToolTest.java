package com.ai.agent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/20 21:33
 */
@SpringBootTest
class WebSearchToolTest {

    @Value("${search.api_key}")
    private String apiKey;

    @Test
    void search() {
        System.out.println(new WebSearchTool(apiKey).search("java"));
    }
}