package com.ahuralearn.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Lazy
    @Resource
    private EmbeddingStore<TextSegment> milvusEmbeddingStore;

    @Bean
    public ContentRetriever contentRetriever(){
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(milvusEmbeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(3)
                .minScore(0.75)
                .build();
        return retriever;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmupMilvusAsync() {
        log.info("Starting Milvus asynchronous warmup...");
        new Thread(() -> {
            try {
                // Execute a dummy search to trigger Milvus initialization & loadCollection
                milvusEmbeddingStore.search(
                        EmbeddingSearchRequest.builder()
                                .queryEmbedding(dev.langchain4j.data.embedding.Embedding.from(new float[1024])) // Match dimension
                                .maxResults(1)
                                .build()
                );
                log.info("Milvus asynchronous warmup completed successfully.");
            } catch (Exception e) {
                log.warn("Milvus warmup encountered expected timeout or error, it should be ready soon. (Msg: {})", e.getMessage());
            }
        }, "Milvus-Warmup-Thread").start();
    }
}
