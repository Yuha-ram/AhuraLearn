package com.ahuralearn.adaptiveexam.ai.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

import dev.langchain4j.service.AiServices;
import com.ahuralearn.adaptiveexam.ai.assistant.AdaptiveAssessmentAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {



    @Bean
    public AdaptiveAssessmentAssistant adaptiveAssessmentAssistant(ChatModel chatModel, StreamingChatModel streamingChatModel) {
        return AiServices.builder(AdaptiveAssessmentAssistant.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
