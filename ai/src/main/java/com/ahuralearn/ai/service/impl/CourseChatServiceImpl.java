package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.enums.MessageRole;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
import com.ahuralearn.ai.domain.vo.ChatResponseVO;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.ahuralearn.ai.service.AiCourseChatService;
import com.ahuralearn.ai.service.IChatMessageService;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.ai.service.ICourseChatService;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 AI 问答核心业务实现类
 * </p>
 *
 * <h3>整体流程概述：</h3>
 *
 * <pre>
 * ┌──────────────────────────────────────────────────────────────────┐
 * │  场景一：新会话（sessionId == null）                              │
 * │  1. 创建 chat_session 记录 → 获取 sessionId                      │
 * │  2. RAG 检索课程上下文                                            │
 * │  3. 调用大模型（SystemMessage + RAG上下文 + 用户提问）             │
 * │  4. 保存用户提问到 chat_message（role=user）                      │
 * │  5. 保存 AI 回答到 chat_message（role=assistant）                 │
 * │  6. 更新 chat_session.update_time                                │
 * │  7. 返回 {sessionId, reply}                                      │
 * ├──────────────────────────────────────────────────────────────────┤
 * │  场景二：追问（sessionId != null）                                │
 * │  1. ChatMemoryProvider 自动加载最近 6 条历史记忆                   │
 * │  2. RAG 检索课程上下文                                            │
 * │  3. 调用大模型（SystemMessage + 历史记忆 + RAG上下文 + 用户追问）  │
 * │  4. 保存用户提问到 chat_message（role=user）                      │
 * │  5. 保存 AI 回答到 chat_message（role=assistant）                 │
 * │  6. 更新 chat_session.update_time                                │
 * │  7. 返回 {sessionId, reply}                                      │
 * └──────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>关键架构约束：</h3>
 * <ul>
 * <li>消息持久化完全由本类手动调用 MyBatis-Plus 的 save() 完成，
 * MysqlChatMemoryStore.updateMessages() 保持空实现（只读存储）</li>
 * <li>持久化时序：用户消息和 AI 回复均在大模型返回后统一保存，
 * 避免 ChatMemory 加载历史时重复读取尚未处理的用户消息</li>
 * <li>RAG 检索到的课程文本通过 @UserMessage 模板融入提问中发送给大模型，
 * 不会被持久化到 chat_message 表（上下文隔离）</li>
 * </ul>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseChatServiceImpl implements ICourseChatService {

    private final IChatSessionService chatSessionService;

    private final IChatMessageService chatMessageService;

    /**
     * AI Service —— including AI model & ChatMemoryProvider & RAG
     */
    private final AiCourseChatService aiCourseChatService;

    @Override
    @Transactional
    public ChatResponseVO chat(ChatRequestDTO request) {
        if (request == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        String userMessage = request.getMessage();
        Long sessionId = request.getSessionId();

        if (sessionId == null) { // new chat - generate session Id
            sessionId = chatSessionService.createNewSession(userMessage);
            log.info("New session created, sessionId={}", sessionId);
        }
        // Call AI Model with retry logic for Milvus 503 channel distribution errors
        String aiReply = null;
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                aiReply = aiCourseChatService.chat(sessionId, userMessage);
                break;
            } catch (Exception e) {
                if (i == maxRetries - 1 || !e.getMessage().contains("503")) {
                    throw e;
                }
                log.warn("Milvus channel is not ready (503), retrying in 2 seconds... (Attempt {}/{})", i + 1, maxRetries);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("AI chat interrupted");
                }
            }
        }
        log.info("Model reply finished，sessionId={}", sessionId);

        // save user message & AI reply to Message DB
        chatMessageService.saveMessage(sessionId, MessageRole.USER, userMessage);
        chatMessageService.saveMessage(sessionId, MessageRole.ASSISTANT, aiReply);

        // update session status (update_time)
        chatSessionService.updateSessionTime(sessionId);
        log.info("Session updated, sessionId={}", sessionId);

        return new ChatResponseVO(sessionId, aiReply);
    }

    @Override
    public List<ChatSessionVO> getSessionList() {

        List<ChatSession> sessions = chatSessionService.getHistorySessions();
        if (CollUtils.isEmpty(sessions)) {
            return Collections.emptyList();
        }

        List<ChatSessionVO> vos = new ArrayList<>(sessions.size());
        for (ChatSession session : sessions) {
            ChatSessionVO vo = BeanUtils.copyBean(session, ChatSessionVO.class);
            vo.setSessionId(session.getId());
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<ChatMessageVO> getMessageHistory(Long sessionId) {
        // verify whether the sessionId is valid
        ChatSession session = validateSection(sessionId);

        // query all messages of the session
        List<ChatMessage> messages = chatMessageService.getMessagesBySessionId(sessionId);
        if (CollUtils.isEmpty(messages))
            return Collections.emptyList();

        // assemble vo list
        return messages.stream().map(msg -> {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setMessageId(msg.getId());
            vo.setRole(msg.getRole().getDesc());
            vo.setContent(msg.getContent());
            return vo;
        }).collect(Collectors.toList());
    }

    private ChatSession validateSection(Long sessionId) {
        ChatSession session = chatSessionService.getById(sessionId);
        if (session == null)
            throw new BusinessException("Session does not exist");

        Long userId = UserContext.getUser();
        if (!session.getUserId().equals(userId))
            throw new BusinessException("Unauthorized access to the session");

        return session;
    }
}
