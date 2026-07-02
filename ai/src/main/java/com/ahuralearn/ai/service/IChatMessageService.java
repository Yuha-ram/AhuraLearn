package com.ahuralearn.ai.service;

import com.ahuralearn.ai.domain.dto.ChatMessageBasicDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.enums.ChatMessageType;
import com.ahuralearn.ai.enums.MessageRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Detail table storing individual messages within a session 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
public interface IChatMessageService extends IService<ChatMessage> {

    // 修改：文本消息和课程卡片消息分别提供专用保存入口，避免调用方手写类型分支。
    void saveTextMessage(Long sessionId, MessageRole role, String content);

    void saveCourseCardMessage(Long sessionId, MessageRole role, String payloadJson);

    List<ChatMessage> getMessagesBySessionId(Long sessionId);

    List<ChatMessageBasicDTO> getRecentMessages(Long sessionId);

    Integer getNextSequence(Long sessionId);

    default void saveMessage(Long sessionId, MessageRole role, String content) {
        saveTypedMessage(sessionId, role, ChatMessageType.TEXT, content, null);
    }

    // 修改：保留底层通用写入入口，供事务性持久化服务统一复用。
    void saveTypedMessage(Long sessionId, MessageRole role, ChatMessageType messageType, String content, String payloadJson);
}
