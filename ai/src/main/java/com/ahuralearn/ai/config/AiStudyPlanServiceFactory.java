package com.ahuralearn.ai.config;

import com.ahuralearn.ai.service.AiStudyPlanAgent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiStudyPlanServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Bean
    public AiStudyPlanAgent aiStudyPlanAgent() {
        return AiServices.builder(AiStudyPlanAgent.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .build();
    }
}
