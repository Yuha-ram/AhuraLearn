package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.service.CourseVectorSyncService;
import com.ahuralearn.ai.utils.CourseEmbeddingFormatter;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.dto.CourseVectorDTO;
import com.ahuralearn.course.service.ICourseService;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseVectorSyncServiceImpl implements CourseVectorSyncService {

    private final ICourseService courseService;

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Lazy
    @Resource
    private EmbeddingStore<TextSegment> milvusEmbeddingStore;

    @Override
    public void syncCoursesToVectorDb() {
        List<CourseVectorDTO> courses = courseService.getCourseMetadata();
        if (CollUtils.isEmpty(courses)) {
            log.warn("No course found, skipping vector sync.");
            return;
        }
        log.info("Starting vector sync for {} courses.", courses.size());
        // 1. format the course data
        List<TextSegment> segments = new ArrayList<>(courses.size());
        List<String> ids = new ArrayList<>(courses.size());
        for (CourseVectorDTO course : courses) {
            String embeddingText = CourseEmbeddingFormatter.courseFormatter(course);
            if (StringUtils.isBlank(embeddingText)) {
                log.warn("Skipping course [id={}] due to empty formatted text.", course.getId());
                continue;
            }
            // primary key
            ids.add(String.valueOf(course.getId()));

            // metadata
            Metadata metadata = new Metadata();
            metadata.put("courseId", String.valueOf(course.getId()));

            segments.add(TextSegment.from(embeddingText, metadata));
        }

        if (segments.isEmpty()) {
            log.warn("All courses produced empty text, nothing to sync.");
            return;
        }
        // 2. embedding course data
        Response<List<Embedding>> response = qwenEmbeddingModel.embedAll(segments);
        List<Embedding> embeddings = response.content();
        log.info("Successfully generated {} embeddings.", embeddings.size());
        // 3. save to Milvus
        milvusEmbeddingStore.addAll(ids, embeddings, segments);
        log.info("Successfully synced {} course vectors to Milvus.", segments.size());
    }
}