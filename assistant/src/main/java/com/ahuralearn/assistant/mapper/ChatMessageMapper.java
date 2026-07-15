package com.ahuralearn.assistant.mapper;

import com.ahuralearn.assistant.domain.po.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AI tutor chat message table Mapper
 * </p>
 * Inherits MyBatis-Plus {@link BaseMapper} CRUD. The feature only needs scoped
 * queries by user/document, so no custom statements are declared here.
 *
 * @author Dariush
 * @since 2026-07-03
 */
@Repository("assistantChatMessageMapper")
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}
