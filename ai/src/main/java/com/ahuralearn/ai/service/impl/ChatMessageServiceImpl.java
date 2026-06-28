package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.dto.ChatMessageBasicDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
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
    public List<ChatMessageBasicDTO> getRecentMessages(Long sessionId) {
        Page<ChatMessage> page = lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getCreateTime)
                .page(new Page<>(1, 6, false));
        List<ChatMessage> records = page.getRecords();
        if (CollUtils.isEmpty(records))
            return CollUtils.emptyList();

        List<ChatMessage> list = CollUtils.reverse(records);
        return BeanUtils.copyList(list, ChatMessageBasicDTO.class);
    }

    @Override
    public void saveMessage(Long sessionId, MessageRole role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        save(message);
    }

    @Override
    public List<ChatMessage> getMessagesBySessionId(Long sessionId) {
        return lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime)
                .list();
    }
}
