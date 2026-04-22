package com.ai.agent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * @author alh
 * @description 创建上下文查询增强的工厂
 * @date 2026/4/20 16:34
 */
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出以下内容：
                抱歉，我只能回答恋爱相关的问题，没有别的办法帮到你哦！
                """);
        return ContextualQueryAugmenter.builder()
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .allowEmptyContext(false)
                .build();
    }
}
