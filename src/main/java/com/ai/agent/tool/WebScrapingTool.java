package com.ai.agent.tool;

import com.google.common.eventbus.DeadEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * @author alh
 * @description 网页抓取工具
 * @date 2026/4/20 21:59
 */
public class WebScrapingTool {

    @Tool(description = "Scrap a web page and return the content")
    public String scrapWebPage(@ToolParam(description = "The URL of the web page to scrap") String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (IOException e) {
            return "Failed to scrap web page: " + e.getMessage();
        }

    }
}
