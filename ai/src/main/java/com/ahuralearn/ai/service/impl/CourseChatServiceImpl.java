package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.po.ChatMessage;
import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.ahuralearn.ai.orchestrator.CourseChatOrchestrator;
import com.ahuralearn.ai.service.IChatMessageService;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.ai.service.ICourseChatService;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.UserContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseChatServiceImpl implements ICourseChatService {

    private final CourseChatOrchestrator courseChatOrchestrator;
    private final IChatSessionService chatSessionService;
    private final IChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;

    @Override
    public void chat(ChatRequestDTO request, SseEmitter emitter) {
        // 修改：聊天主流程迁移到编排器，当前服务只保留兼容层职责。
        courseChatOrchestrator.chat(request, emitter);
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
            vo.setStatus(session.getStatus().getDesc());
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<ChatMessageVO> getMessageHistory(Long sessionId) {
        validateSessionOwnership(sessionId);
        List<ChatMessage> messages = chatMessageService.getMessagesBySessionId(sessionId);
        if (CollUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        return messages.stream().map(m->{
            ChatMessageVO vo = new ChatMessageVO();
            return vo.setMessageId(m.getId())
                    .setRole(m.getRole().getDesc())
                    .setMessageType(m.getMessageType().getDesc())
                    .setContent(m.getContent())
                    .setPayload(readPayload(m.getPayloadJson()));
        }).collect(Collectors.toList());
    }

//    private ChatMessageVO toChatMessageVO(ChatMessage message) {
//        ChatMessageVO vo = new ChatMessageVO();
//        vo.setMessageId(message.getId());
//        vo.setRole(message.getRole().getDesc());
//        vo.setMessageType(message.getMessageType().getDesc());
//        vo.setContent(message.getContent());
//        vo.setPayload(readPayload(message.getPayloadJson()));
//        return vo;
//    }

    private JsonNode readPayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(payloadJson);
        } catch (Exception e) {
            log.warn("Failed to parse payloadJson from chat history.", e);
            return null;
        }
    }

    private void validateSessionOwnership(Long sessionId) {
        ChatSession session = chatSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException("Session does not exist");
        }

        Long userId = UserContext.getUser();
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized access to the session");
        }
    }
}
