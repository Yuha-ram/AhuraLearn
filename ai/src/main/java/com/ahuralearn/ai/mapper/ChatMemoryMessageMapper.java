package com.ahuralearn.ai.mapper;

import com.ahuralearn.ai.domain.po.ChatMemoryMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用于读写 LangChain4j 运行时记忆窗口。
 */
public interface ChatMemoryMessageMapper extends BaseMapper<ChatMemoryMessage> {
}
