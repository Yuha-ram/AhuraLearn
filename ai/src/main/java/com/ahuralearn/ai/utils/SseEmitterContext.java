package com.ahuralearn.ai.utils;

import com.ahuralearn.ai.sse.ChatStreamContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SseEmitterContext {

    private static final Map<Long, ChatStreamContext> CONTEXT = new ConcurrentHashMap<>();

    private SseEmitterContext() {
    }

    public static boolean set(ChatStreamContext context) {
        // 修改：同一会话只允许注册一个活跃流，避免课程卡片发到错误连接。
        return CONTEXT.putIfAbsent(context.getSessionId(), context) == null;
    }

    public static ChatStreamContext get(Long sessionId) {
        return CONTEXT.get(sessionId);
    }

    public static void clear(Long sessionId) {
        CONTEXT.remove(sessionId);
    }
}
