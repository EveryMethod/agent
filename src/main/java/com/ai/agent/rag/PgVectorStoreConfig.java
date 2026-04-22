//package com.ai.agent.rag;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.List;
//
///**
// * @author alh
// * @description pgsql向量数据库配置类
// * @date 2026/4/20 10:22
// */
////@Configuration
//public class PgVectorStoreConfig {
//
//    @Resource
//    private LoveAppDocumentLoader loveAppDocumentLoader;
//
//    @Bean
//    public VectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
//        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
//                .dimensions(1536)
//                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
//                .indexType(PgVectorStore.PgIndexType.HNSW)
//                .initializeSchema(true)
//                .schemaName("public")
//                .vectorTableName("vector_store")
//                .maxDocumentBatchSize(10000)
//                .build();
//        List<Document> documents = loveAppDocumentLoader.loadMarkdownDoc();
//        pgVectorStore.doAdd(documents);
//        return pgVectorStore;
//    }
//}
