package com.ahuralearn.ai.config;
import com.ahuralearn.ai.domain.dto.ChatMessageBasicDTO;
import com.ahuralearn.ai.enums.MessageRole;
import com.ahuralearn.ai.service.IChatMessageService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MysqlChatMemoryStore implements ChatMemoryStore {

    private final IChatMessageService chatMessageService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Long sessionId = Long.valueOf(memoryId.toString());
        List<ChatMessageBasicDTO> recentMessages = chatMessageService.getRecentMessages(sessionId);

        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageBasicDTO m : recentMessages) {
            if (MessageRole.USER == m.getRole()) {
                messages.add(UserMessage.from(m.getContent()));
            } else if (MessageRole.ASSISTANT == m.getRole()) {
                messages.add(AiMessage.from(m.getContent()));
            }
        }
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {

    }

    @Override
    public void deleteMessages(Object memoryId) {

    }
}
