package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.domain.vo.CourseCardPayloadVO;
import com.ahuralearn.ai.enums.MessageRole;
import com.ahuralearn.ai.enums.SessionStatus;
import com.ahuralearn.ai.service.ChatHistoryPersistenceService;
import com.ahuralearn.ai.service.IChatMessageService;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.ai.sse.ChatStreamBlock;
import com.ahuralearn.common.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用于保证历史消息和会话状态更新在同一事务内完成。
 */
@Service
@RequiredArgsConstructor
public class ChatHistoryPersistenceServiceImpl implements ChatHistoryPersistenceService {

    private final IChatMessageService chatMessageService;
    private final IChatSessionService chatSessionService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void persistCompletedRound(Long sessionId,
                                      String userMessage,
                                      List<ChatStreamBlock> assistantBlocks,
                                      String fullAssistantReply) {
        chatMessageService.saveTextMessage(sessionId, MessageRole.USER, userMessage);

        if (assistantBlocks == null || assistantBlocks.isEmpty()) {
            chatMessageService.saveTextMessage(sessionId, MessageRole.ASSISTANT, fullAssistantReply);
        } else {
            for (ChatStreamBlock block : assistantBlocks) {
                if (block == null || block.getMessageType() == null) {
                    continue;
                }

                switch (block.getMessageType()) {
                    case TEXT -> {
                        if (StringUtils.isNotBlank(block.getContent())) {
                            chatMessageService.saveTextMessage(sessionId, MessageRole.ASSISTANT, block.getContent());
                        }
                    }
                    case COURSE_CARD -> {
                        if (block.getCourseCardPayload() != null) {
                            chatMessageService.saveCourseCardMessage(sessionId, MessageRole.ASSISTANT, writePayload(block.getCourseCardPayload()));
                        }
                    }
                    default -> {
                    }
                }
            }
        }

        chatSessionService.updateSessionStatus(sessionId, SessionStatus.ACTIVE);
        chatSessionService.updateSessionTime(sessionId);
    }

    @Override
    @Transactional
    public void markSessionFailedIfPending(Long sessionId) {
        ChatSession session = chatSessionService.getById(sessionId);
        if (session != null && SessionStatus.PENDING == session.getStatus()) {
            chatSessionService.updateSessionStatus(sessionId, SessionStatus.FAILED);
            chatSessionService.updateSessionTime(sessionId);
        }
    }

    private String writePayload(CourseCardPayloadVO courseCard) {
        try {
            return objectMapper.writeValueAsString(courseCard);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize course card payload", e);
        }
    }
}
