package com.ai.agent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * @author alh
 * @description 查询重写器
 * @date 2026/4/20 15:07
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashscopeChatModel) {
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .build();
    }

    /**
     * 执行查询重写
     * @param prompt 用户提示词
     * @return 重写后的用户提示词
     */
    public String doQueryRewrite(String prompt) {
        return queryTransformer.transform(new Query(prompt)).text();
    }
}
