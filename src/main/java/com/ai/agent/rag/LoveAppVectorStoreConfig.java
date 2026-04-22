package com.ai.agent.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author alh
 * @description 恋爱大师向量数据库配置
 * @date 2026/4/18 15:20
 */
@Configuration
@RequiredArgsConstructor
public class LoveAppVectorStoreConfig {

    private final LoveAppDocumentLoader documentLoader;

    private final MyKeyWordEnricher enricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel model) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(model).build();
        List<Document> documents = enricher.enrichDocuments(documentLoader.loadMarkdownDoc());
        vectorStore.add(documents);
        return vectorStore;
    }

}
