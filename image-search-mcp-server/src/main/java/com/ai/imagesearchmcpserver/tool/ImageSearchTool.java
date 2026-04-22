package com.ai.imagesearchmcpserver.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alh
 * @description 图片搜索mcp服务
 * @date 2026/4/21 17:28
 */
@Service
public class ImageSearchTool {

    @Value("${imageSearchTool.apiKey}")
    private String apiKey;

    @Value("${imageSearchTool.apiUrl}")
    private  String apiUrl;
    
    @Tool(description = "search image from web")
    public String searchImage(@ToolParam(description = "search query keyword") String query) {
        try {
            return String.join(",",searchMediumImages(query));
        } catch (Exception e) {
            return "Error search image: " + e.getMessage();
        }
    }

    public List<String> searchMediumImages(String query) {
        // 设置请求头
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiKey);

        // 设置请求参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("query", query);

        // 发送http请求
        String response = HttpUtil.createGet(apiUrl)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(obj -> (JSONObject) obj)
                .map(obj -> obj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
