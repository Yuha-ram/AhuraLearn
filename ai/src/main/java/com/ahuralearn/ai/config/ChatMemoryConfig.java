package com.ahuralearn.ai.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider(MysqlChatMemoryStore mysqlStore) {
        // 【追问上下文增强】工具调用会占用多条 memory，12 条能更稳地保留最近推荐课程的 courseId 锚点。
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(12)
                .chatMemoryStore(mysqlStore)
                .build();
    }
}
