package com.ai.agent.tool;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author alh
 * @description 网页搜索工具
 * @date 2026/4/20 21:15
 */
public class WebSearchTool {

    private final String SEARCH_API = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search information from baidu Search Engine")
    public String search(@ToolParam(description = "Search query keyWord") String query) {
        HashMap<String, Object> param = new HashMap<>(3);
        param.put("q",query);
        param.put("api_key",apiKey);
        param.put("engine","baidu");
        try {
            String response = HttpUtil.get(SEARCH_API, param);
            // 取出返回结果中的5条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            return jsonObject.getJSONArray("organic_results").subList(0, 3).stream()
                    .map(obj -> {
                        JSONObject tmpObject = (JSONObject) obj;
                        return tmpObject.toString();
                    }).collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching baidu: " + e.getMessage();
        }
    }
}
