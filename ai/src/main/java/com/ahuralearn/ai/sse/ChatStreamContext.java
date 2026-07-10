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
    private final List<ChatStreamBlock> assistantBlocks = new ArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

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

        ChatStreamBlock lastBlock = assistantBlocks.isEmpty() ? null : assistantBlocks.get(assistantBlocks.size() - 1);
        if (lastBlock != null && lastBlock.getMessageType() == ChatMessageType.TEXT) {
            lastBlock.appendContent(token);
        } else {
            assistantBlocks.add(ChatStreamBlock.text(token));
        }
    }

    public synchronized void addCourseCard(CourseCardPayloadVO courseCard) {
        assistantBlocks.add(ChatStreamBlock.courseCard(courseCard));
    }

    public synchronized String getIntroText() {
        return collectTextBlocksBeforeFirstCourseCard().trim();
    }

    public synchronized String getReasonText() {
        return collectTextBlocksAfterFirstCourseCard().trim();
    }

    public synchronized List<CourseCardPayloadVO> getCourseCards() {
        List<CourseCardPayloadVO> courseCards = new ArrayList<>();
        for (ChatStreamBlock block : assistantBlocks) {
            if (block.getMessageType() == ChatMessageType.COURSE_CARD) {
                courseCards.add(block.getCourseCardPayload());
            }
        }
        return Collections.unmodifiableList(courseCards);
    }

    public synchronized List<ChatStreamBlock> getAssistantBlocks() {
        List<ChatStreamBlock> blocks = new ArrayList<>(assistantBlocks.size());
        for (ChatStreamBlock block : assistantBlocks) {
            blocks.add(block.copy());
        }
        return blocks;
    }

    public synchronized boolean hasCourseCards() {
        return assistantBlocks.stream().anyMatch(block -> block.getMessageType() == ChatMessageType.COURSE_CARD);
    }

    private String collectTextBlocksBeforeFirstCourseCard() {
        StringBuilder text = new StringBuilder();
        for (ChatStreamBlock block : assistantBlocks) {
            if (block.getMessageType() == ChatMessageType.COURSE_CARD) {
                break;
            }
            appendTextBlock(text, block);
        }
        return text.toString();
    }

    private String collectTextBlocksAfterFirstCourseCard() {
        StringBuilder text = new StringBuilder();
        boolean firstCourseCardFound = false;
        for (ChatStreamBlock block : assistantBlocks) {
            if (block.getMessageType() == ChatMessageType.COURSE_CARD) {
                firstCourseCardFound = true;
                continue;
            }
            if (firstCourseCardFound) {
                appendTextBlock(text, block);
            }
        }
        return text.toString();
    }

    private void appendTextBlock(StringBuilder target, ChatStreamBlock block) {
        if (block.getMessageType() == ChatMessageType.TEXT && block.getContent() != null) {
            target.append(block.getContent());
        }
    }

    public boolean isClosed() {
        return closed.get();
    }

    public boolean markClosed() {
        return closed.compareAndSet(false, true);
    }
}
