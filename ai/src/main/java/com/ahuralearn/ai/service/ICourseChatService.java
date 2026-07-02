package com.ahuralearn.ai.service;

import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ICourseChatService {

//    ChatResponseVO chat(ChatRequestDTO request);
    void chat(ChatRequestDTO request, SseEmitter emitter);

    /**
     * 获取当前用户的历史会话列表
     * <p>
     * 查询当前登录用户所有未被逻辑删除的会话，按最后活跃时间降序排列，
     * 用于前端侧边栏的会话导航列表渲染。
     * </p>
     *
     * @return 会话摘要 VO 列表（按 update_time DESC 排序）
     */
    List<ChatSessionVO> getSessionList();

    /**
     * 获取指定会话的完整聊天记录
     * <p>
     * 根据 sessionId 查询该会话下所有消息，按创建时间升序排列，
     * 用于前端聊天主界面的气泡渲染。
     * 内含越权校验：若该会话不属于当前登录用户，将抛出 BusinessException。
     * </p>
     *
     * @param sessionId 目标会话 ID
     * @return 消息 VO 列表（按 create_time ASC 排序）
     */
    List<ChatMessageVO> getMessageHistory(Long sessionId);
}

