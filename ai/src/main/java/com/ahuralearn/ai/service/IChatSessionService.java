package com.ahuralearn.ai.service;

import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Master table storing chat session metadata for UI sidebar 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
public interface IChatSessionService extends IService<ChatSession> {
    Long createNewSession(String firstMessage);

    void updateSessionTime(Long sessionId);

    List<ChatSession> getHistorySessions();
}
