package com.ahuralearn.ai.service;

import com.ahuralearn.ai.sse.ChatStreamBlock;

import java.util.List;

/**
 * 用于统一管理用户可见历史消息的事务性持久化。
 */
public interface ChatHistoryPersistenceService {

    void persistCompletedRound(Long sessionId,
                               String userMessage,
                               List<ChatStreamBlock> assistantBlocks,
                               String fullAssistantReply);

    void markSessionFailedIfPending(Long sessionId);
}
