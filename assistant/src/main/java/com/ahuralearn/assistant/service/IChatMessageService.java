package com.ahuralearn.assistant.service;

import com.ahuralearn.assistant.domain.po.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * AI tutor chat message table service
 * </p>
 * Extends MyBatis-Plus {@link IService} (generic CRUD + lambdaQuery is inherited);
 * the assistant service composes its scoped history queries on top of it.
 *
 * @author Dariush
 * @since 2026-07-03
 */
public interface IChatMessageService extends IService<ChatMessage> {

}
