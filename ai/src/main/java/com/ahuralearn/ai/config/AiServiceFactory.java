package com.ahuralearn.ai.config;

import com.ahuralearn.ai.service.AiCourseChatService;
import com.ahuralearn.ai.tools.CourseDetailTool;
import com.ahuralearn.ai.tools.CourseRecommendationTool;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private ContentRetriever customContentRetriever;

    @Resource
    private ChatMemoryProvider chatMemoryProvider;

    @Resource
    private QueryTransformer CourseIntentQueryTransformer;

    @Resource
    private CourseRecommendationTool courseRecommendationTool;

    @Resource
    private CourseDetailTool courseDetailTool;

    @Bean
    public AiCourseChatService aiCourseChatService() {
        // assemble RAG Augmentor
        DefaultRetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(CourseIntentQueryTransformer)
                .contentRetriever(customContentRetriever)
                .build();

        return AiServices.builder(AiCourseChatService.class)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .retrievalAugmentor(augmentor)
                // 【追问能力增强】推荐工具负责推课程卡片；详情工具负责在追问时按 courseId 查询权威课程详情。
                .tools(courseRecommendationTool, courseDetailTool)
                .build();
    }
}
