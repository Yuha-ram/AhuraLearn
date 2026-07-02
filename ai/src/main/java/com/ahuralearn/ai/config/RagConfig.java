package com.ahuralearn.ai.config;

import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.service.ICourseService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Lazy
    @Resource
    private EmbeddingStore<TextSegment> milvusEmbeddingStore;

    @Resource
    private ICourseService courseService;

    @Bean
    public ContentRetriever customContentRetriever() {
        EmbeddingStoreContentRetriever milvusRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(milvusEmbeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(3)
                .minScore(0.55)
                .build();

        return query -> {
            List<Content> contents = milvusRetriever.retrieve(query);
            log.info("Retrieved {} segments from Milvus for query: {}", contents.size(), query.text());
            if (CollUtils.isEmpty(contents)) {
                log.warn("No course segments found. Injecting fallback context.");
                // tell the Model not to improvise but allow follow-up answers
                String context = "[SYSTEM INSTRUCTION: No new course documents were retrieved for the current query. " +
                        "If the user is asking a follow-up question about a previously discussed course, use your conversation memory to answer. " +
                        "If it's a new request, politely inform the user that the platform does not contain such courses yet. " +
                        "You are strictly forbidden from recommending any course names that are not in the context!]";
                return List.of(Content.from(TextSegment.from(context)));
            }

            // Extract IDs
            List<Long> courseIds = contents.stream()
                    .map(c -> {
                        String courseId = c.textSegment().metadata().getString("courseId");
                        if (courseId == null) {
                            courseId = c.textSegment().metadata().getString("id");
                        }
                        log.info("Extracted courseId: {} from metadata: {}", courseId,
                                c.textSegment().metadata().toMap());
                        return courseId;
                    })
                    .filter(Objects::nonNull)
                    .map(Long::valueOf)
                    .distinct()
                    .collect(Collectors.toList());

            if (CollUtils.isEmpty(courseIds)) {
                log.warn("No courseIds extracted from Milvus segments!");
                return contents;
            }

            // Batch query MySQL
            List<Course> courses = courseService.listByIds(courseIds);
            log.info("Queried MySQL for courseIds {}, found {} courses", courseIds, courses.size());

            Map<Long, Course> courseMap = courses.stream()
                    .collect(Collectors.toMap(Course::getId, c -> c));

            // Enrich text
            return contents.stream().map(c -> {
                TextSegment segment = c.textSegment();
                String courseIdStr = segment.metadata().getString("courseId");
                if (courseIdStr == null) {
                    courseIdStr = segment.metadata().getString("id");
                }

                if (courseIdStr != null) {
                    Long id = Long.valueOf(courseIdStr);
                    Course course = courseMap.get(id);
                    if (course != null) {
                        String enrichedText = String.format(
                                "%s\n\n[Course Information] ID: %d, Name: %s",
                                segment.text(), course.getId(), course.getName());
                        log.info("Successfully enriched segment with course id={}", id);
                        return Content.from(TextSegment.from(enrichedText, segment.metadata()));
                    } else {
                        log.warn("Course id={} not found in MySQL!", id);
                    }
                }
                return c;
            }).collect(Collectors.toList());
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmupMilvusAsync() {
        log.info("Starting Milvus asynchronous warmup...");
        new Thread(() -> {
            try {
                // Execute a dummy search to trigger Milvus initialization & loadCollection
                milvusEmbeddingStore.search(
                        EmbeddingSearchRequest.builder()
                                .queryEmbedding(Embedding.from(new float[1024])) // Match dimension
                                .maxResults(1)
                                .build());
                log.info("Milvus asynchronous warmup completed successfully.");
            } catch (Exception e) {
                log.warn("Milvus warmup encountered expected timeout or error, it should be ready soon. (Msg: {})",
                        e.getMessage());
            }
        }, "Milvus-Warmup-Thread").start();
    }
}
