package com.ahuralearn.ai;

import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.common.clientenum.ConsistencyLevelEnum;

public class TestBuilder {
    public static void main(String[] args) {
        try {
            MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
                    .host("localhost")
                    .port(19530)
                    .collectionName("ahuralearn_courses")
                    .dimension(1024)
                    .indexType(IndexType.HNSW)
                    .metricType(MetricType.COSINE)
                    .consistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                    .autoFlushOnInsert(true)
                    .idFieldName("id")
                    .textFieldName("text")
                    .metadataFieldName("metadata")
                    .vectorFieldName("vector")
                    .build();
            store.dropCollection("ahuralearn_courses");
            System.out.println("Collection dropped successfully.");
        } catch (Exception e) {
            System.err.println("Failed to drop collection: " + e.getMessage());
        }
    }
}
