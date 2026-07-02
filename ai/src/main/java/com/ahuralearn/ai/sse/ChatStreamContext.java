package com.ahuralearn.ai.sse;

import com.ahuralearn.ai.domain.vo.CourseCardPayloadVO;
import com.ahuralearn.ai.enums.ChatMessageType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于保存一次流式会话的运行态上下文，避免将临时状态散落在多个类中。
 */
public class ChatStreamContext {

    private final Long sessionId;
    private final SseEmitter emitter;
    private final StringBuilder introBuffer = new StringBuilder();
    private final StringBuilder reasonBuffer = new StringBuilder();
    private final List<CourseCardPayloadVO> courseCards = new ArrayList<>();
    private final List<ChatStreamBlock> assistantBlocks = new ArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile boolean courseCardSent;

    public ChatStreamContext(Long sessionId, SseEmitter emitter) {
        this.sessionId = sessionId;
        this.emitter = emitter;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }

    public synchronized void appendAssistantText(String token) {
        if (token == null) {
            return;
        }
        if (courseCardSent) {
            reasonBuffer.append(token);
        } else {
            introBuffer.append(token);
        }

        ChatStreamBlock lastBlock = assistantBlocks.isEmpty() ? null : assistantBlocks.get(assistantBlocks.size() - 1);
        if (lastBlock != null && lastBlock.getMessageType() == ChatMessageType.TEXT) {
            lastBlock.appendContent(token);
        } else {
            assistantBlocks.add(ChatStreamBlock.text(token));
        }
    }

    public synchronized void addCourseCard(CourseCardPayloadVO courseCard) {
        courseCards.add(courseCard);
        assistantBlocks.add(ChatStreamBlock.courseCard(courseCard));
        courseCardSent = true;
    }

    public String getIntroText() {
        return introBuffer.toString().trim();
    }

    public String getReasonText() {
        return reasonBuffer.toString().trim();
    }

    public List<CourseCardPayloadVO> getCourseCards() {
        return Collections.unmodifiableList(courseCards);
    }

    public synchronized List<ChatStreamBlock> getAssistantBlocks() {
        List<ChatStreamBlock> blocks = new ArrayList<>(assistantBlocks.size());
        for (ChatStreamBlock block : assistantBlocks) {
            blocks.add(block.copy());
        }
        return blocks;
    }

    public boolean hasCourseCards() {
        return !courseCards.isEmpty();
    }

    public boolean isClosed() {
        return closed.get();
    }

    public boolean markClosed() {
        return closed.compareAndSet(false, true);
    }
}
