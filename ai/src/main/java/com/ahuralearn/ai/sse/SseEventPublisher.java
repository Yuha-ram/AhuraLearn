package com.ahuralearn.ai.sse;

import com.ahuralearn.ai.domain.vo.CourseCardPayloadVO;
import com.ahuralearn.ai.domain.vo.SseErrorEventVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 用于统一管理 SSE 事件的发送格式和细节。
 */
@Component
@RequiredArgsConstructor
public class SseEventPublisher {

    private final ObjectMapper objectMapper;

    public void publishSessionId(SseEmitter emitter, Long sessionId) throws Exception {
        emitter.send(SseEmitter.event()
                .name(SseEventNames.SESSION_ID)
                .data(sessionId.toString()));
    }

    public void publishText(SseEmitter emitter, String token) throws Exception {
        String safeToken = token.replace("\n", "\\n");
        emitter.send(SseEmitter.event()
                .name(SseEventNames.TEXT)
                .data(" " + safeToken));
    }

    public void publishCourseCard(SseEmitter emitter, CourseCardPayloadVO courseCard) throws Exception {
        emitter.send(SseEmitter.event()
                .name(SseEventNames.UI_COURSE)
                .data(objectMapper.writeValueAsString(courseCard)));
    }

    public void publishError(SseEmitter emitter, String code, String message, boolean retryable) throws Exception {
        SseErrorEventVO payload = new SseErrorEventVO(code, message, retryable);
        emitter.send(SseEmitter.event()
                .name(SseEventNames.ERROR)
                .data(objectMapper.writeValueAsString(payload)));
    }

    public void publishDone(SseEmitter emitter) throws Exception {
        emitter.send(SseEmitter.event()
                .name(SseEventNames.DONE)
                .data("done"));
    }
}
