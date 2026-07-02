package com.ahuralearn.ai.config;

import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MilvusConfig {

    @Lazy
    @Bean
    public MilvusEmbeddingStore milvusEmbeddingStore(){
        MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
                .host("localhost")                         // Host for Milvus instance
                .port(19530)                               // Port for Milvus instance
                .collectionName("ahuralearn_courses")      // Name of the collection
                .dimension(1024)                            // Dimension of vectors
                .indexType(IndexType.FLAT)                 // Index type (FLAT requires no extra parameters)
                .metricType(MetricType.COSINE)             // Metric type
                .consistencyLevel(ConsistencyLevelEnum.EVENTUALLY)  // Consistency level
                .autoFlushOnInsert(true)                   // Auto flush after insert
                .idFieldName("id")                         // ID field name
                .textFieldName("text")                     // Text field name
                .metadataFieldName("metadata")             // Metadata field name
                .vectorFieldName("vector")                 // Vector field name
                .build();

        return store;
    }
}
