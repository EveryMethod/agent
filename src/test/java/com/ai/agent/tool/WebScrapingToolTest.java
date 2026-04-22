package com.ai.agent.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author alh
 * @description
 * @date 2026/4/20 22:02
 */
class WebScrapingToolTest {

    @Test
    void scrapWebPage() {
        String result = new WebScrapingTool().scrapWebPage("https://www.codefather.cn");
        System.out.println(result);
    }
}