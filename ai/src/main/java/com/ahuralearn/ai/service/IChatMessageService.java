package com.ahuralearn.ai.service;

import com.ahuralearn.ai.domain.dto.ChatMessageBasicDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
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

    List<ChatMessageBasicDTO> getRecentMessages(Long sessionId);

    void saveMessage(Long sessionId, MessageRole role, String content);

    List<ChatMessage> getMessagesBySessionId(Long sessionId);
}
