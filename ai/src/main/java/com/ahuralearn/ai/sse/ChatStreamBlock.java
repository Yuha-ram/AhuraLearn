package com.ahuralearn.ai.sse;

import com.ahuralearn.ai.domain.vo.CourseCardPayloadVO;
import com.ahuralearn.ai.enums.ChatMessageType;

/**
 * Represents one assistant-rendered block in stream order so history replay can
 * match the live SSE experience exactly.
 */
public class ChatStreamBlock {

    private final ChatMessageType messageType;
    private String content;
    private final CourseCardPayloadVO courseCardPayload;

    private ChatStreamBlock(ChatMessageType messageType, String content, CourseCardPayloadVO courseCardPayload) {
        this.messageType = messageType;
        this.content = content;
        this.courseCardPayload = courseCardPayload;
    }

    public static ChatStreamBlock text(String content) {
        return new ChatStreamBlock(ChatMessageType.TEXT, content, null);
    }

    public static ChatStreamBlock courseCard(CourseCardPayloadVO courseCardPayload) {
        return new ChatStreamBlock(ChatMessageType.COURSE_CARD, null, courseCardPayload);
    }

    public ChatMessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public void appendContent(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        content = (content == null ? "" : content) + token;
    }

    public CourseCardPayloadVO getCourseCardPayload() {
        return courseCardPayload;
    }

    public ChatStreamBlock copy() {
        if (messageType == ChatMessageType.COURSE_CARD) {
            CourseCardPayloadVO payload = null;
            if (courseCardPayload != null) {
                payload = new CourseCardPayloadVO();
                payload.setId(courseCardPayload.getId());
                payload.setName(courseCardPayload.getName());
                payload.setCoverUrl(courseCardPayload.getCoverUrl());
                payload.setDifficultyLevel(courseCardPayload.getDifficultyLevel());
            }
            return ChatStreamBlock.courseCard(payload);
        }
        return ChatStreamBlock.text(content);
    }
}
