package com.ahuralearn.ai.sse;

/**
 * 用于统一管理 SSE 事件名称。
 */
public final class SseEventNames {

    public static final String SESSION_ID = "session_id";
    public static final String TEXT = "text";
    public static final String UI_COURSE = "ui_course";
    public static final String ERROR = "error";
    public static final String DONE = "done";

    private SseEventNames() {
    }
}
