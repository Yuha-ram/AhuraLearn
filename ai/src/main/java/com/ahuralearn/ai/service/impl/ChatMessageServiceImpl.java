package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.dto.ChatMessageBasicDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.enums.ChatMessageType;
import com.ahuralearn.ai.enums.MessageRole;
import com.ahuralearn.ai.mapper.ChatMessageMapper;
import com.ahuralearn.ai.service.IChatMessageService;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Detail table storing individual messages within a session 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    @Override
    public void saveTextMessage(Long sessionId, MessageRole role, String content) {
        saveTypedMessage(sessionId, role, ChatMessageType.TEXT, content, null);
    }

    @Override
    public void saveCourseCardMessage(Long sessionId, MessageRole role, String payloadJson) {
        saveTypedMessage(sessionId, role, ChatMessageType.COURSE_CARD, "", payloadJson);
    }

    @Override
    public List<ChatMessage> getMessagesBySessionId(Long sessionId) {
        return lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getSequence)
                .list();
    }

    @Override
    // TODO 可以考虑删除
    public List<ChatMessageBasicDTO> getRecentMessages(Long sessionId) {
        // 修改：memory 只读取可参与对话上下文的文本消息，不读取课程卡片消息。
        Page<ChatMessage> page = lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getMessageType, ChatMessageType.TEXT)
                .orderByDesc(ChatMessage::getSequence)
                .page(new Page<>(1, 6, false));
        List<ChatMessage> records = page.getRecords();
        if (CollUtils.isEmpty(records))
            return CollUtils.emptyList();

        List<ChatMessage> list = CollUtils.reverse(records);
        return BeanUtils.copyList(list, ChatMessageBasicDTO.class);
    }

    @Override
    public Integer getNextSequence(Long sessionId) {
        ChatMessage lastMessage = lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getSequence)
                .last("LIMIT 1")
                .one();
        return lastMessage == null || lastMessage.getSequence() == null ? 1 : lastMessage.getSequence() + 1;
    }

    @Override
    public void saveTypedMessage(Long sessionId,
                                 MessageRole role,
                                 ChatMessageType messageType,
                                 String content,
                                 String payloadJson) {
        // 修改：统一在这里落库消息类型和可选 payload，避免多处散落相同构造逻辑。
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setPayloadJson(payloadJson);
        message.setSequence(getNextSequence(sessionId));
        save(message);
    }
}
