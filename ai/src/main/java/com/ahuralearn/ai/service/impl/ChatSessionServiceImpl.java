package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.ahuralearn.ai.enums.SessionStatus;
import com.ahuralearn.ai.mapper.ChatSessionMapper;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Master table storing chat session metadata for UI sidebar 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements IChatSessionService {

    @Override
    public Long createNewSession(String firstMessage) {
        ChatSession session = new ChatSession();
        session.setUserId(UserContext.getUser());
        // cut the first 20 chars as session name
        String title = firstMessage.length() > 20
                ? firstMessage.substring(0, 20) + "..."
                : firstMessage;
        session.setTitle(title);

        save(session);
        return session.getId(); // return the new session id
    }

    @Override
    public void updateSessionTime(Long sessionId) {
        ChatSession update = new ChatSession();
        update.setId(sessionId);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public List<ChatSession> getHistorySessions() {
        Long userId = UserContext.getUser();
        return lambdaQuery()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getStatus, SessionStatus.ACTIVE)
                .orderByDesc(ChatSession::getUpdateTime) // the latest session will be first
                .list();
    }
}
