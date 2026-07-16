package com.ahuralearn.assistant.service.impl;

import com.ahuralearn.assistant.domain.po.ChatMessage;
import com.ahuralearn.assistant.mapper.ChatMessageMapper;
import com.ahuralearn.assistant.service.IChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * AI tutor chat message table service impl
 * </p>
 * Plain MyBatis-Plus CRUD over the {@code assistant_chat_message} table; the assistant
 * service composes its scoped history queries on top of the inherited lambdaQuery.
 *
 * @author Dariush
 * @since 2026-07-03
 */
@Service("assistantChatMessageService")
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

}
