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

    // 【AI 标题生成】首轮回答完成后生成英文会话标题，失败时由实现层保留临时标题。
    void generateAndUpdateTitle(Long sessionId, String userMessage, String assistantReply);

    // 【标题落库】只更新 chat_session.title，不参与聊天历史和 memory。
    void updateSessionTitle(Long sessionId, String title);

    // 修改：会话状态更新独立暴露，便于编排层显式管理 pending/active/failed。
    void updateSessionStatus(Long sessionId, com.ahuralearn.ai.enums.SessionStatus status);

    List<ChatSession> getHistorySessions();
}
