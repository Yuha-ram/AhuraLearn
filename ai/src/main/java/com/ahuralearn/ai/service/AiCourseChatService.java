package com.ahuralearn.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * <p>
 * LangChain4j AI Service 接口 —— 课程问答专用
 * </p>
 *
 * <h3>设计要点：</h3>
 * <ul>
 *   <li>使用 {@code @SystemMessage} 加载预置的系统提示词 (system-prompt.txt)，定义 AI 的角色边界</li>
 *   <li>使用 {@code @MemoryId} 绑定会话 ID，LangChain4j 会通过 {@code ChatMemoryProvider}
 *       自动从 {@code MysqlChatMemoryStore} 加载该会话的最近 6 条历史记忆</li>
 *   <li>RAG 检索能力由配置层自动注入，框架会在调用大模型前自动检索并融入上下文，
 *       不污染 ChatMemory 内存与数据库</li>
 * </ul>
 *
 * @author Yorina
 * @since 2026-06-27
 */
public interface AiCourseChatService {

    /**
     * 课程问答核心方法
     *
     *
     * @param memoryId       会话 ID，用于从 MySQL 加载历史记忆
     * @param userMessage    用户的原始提问
     * @return 大模型的回答文本
     */
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}
