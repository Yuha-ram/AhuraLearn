package com.ahuralearn.ai.orchestrator;

import com.ahuralearn.ai.config.CourseIntentQueryTransformer;
import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.service.AiCourseChatService;
import com.ahuralearn.ai.service.ChatHistoryPersistenceService;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.ai.sse.ChatStreamContext;
import com.ahuralearn.ai.sse.SseEventPublisher;
import com.ahuralearn.ai.utils.SseEmitterContext;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 用于串联聊天校验、SSE 输出、AI 流式回调和最终历史持久化。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourseChatOrchestrator {

    private final IChatSessionService chatSessionService;
    private final AiCourseChatService aiCourseChatService;
    private final ChatHistoryPersistenceService chatHistoryPersistenceService;
    private final SseEventPublisher sseEventPublisher;

    public void chat(ChatRequestDTO request, SseEmitter emitter) {
        Long sessionId = null;
        ChatStreamContext streamContext = null;
        boolean newSession = false;
        try {
            validateRequest(request);

            sessionId = request.getSessionId();
            newSession = sessionId == null;
            if (sessionId == null) {
                sessionId = chatSessionService.createNewSession(request.getMessage());
            } else {
                validateSessionOwnership(sessionId);
            }

            streamContext = new ChatStreamContext(sessionId, emitter);
            if (!SseEmitterContext.set(streamContext)) {
                throw new BusinessException("Current session is already generating a response.");
            }
            attachEmitterLifecycle(streamContext);

            sseEventPublisher.publishSessionId(emitter, sessionId);

            final Long finalSessionId = sessionId;
            final ChatStreamContext finalStreamContext = streamContext;
            final boolean finalNewSession = newSession;
            TokenStream tokenStream = aiCourseChatService.chat(finalSessionId, request.getMessage());
            tokenStream.onPartialResponse(token -> handlePartialResponse(finalStreamContext, token))
                    .onCompleteResponse(response -> handleCompleteResponse(finalSessionId, request.getMessage(), finalStreamContext, response.aiMessage().text(), finalNewSession))
                    .onError(error -> handleError(finalSessionId, request.getMessage(), finalStreamContext, error))
                    .start();
        } catch (Exception e) {
            if (sessionId != null && streamContext != null && isOutOfScopeError(e)) {
                handleOutOfScopeReply(sessionId, request.getMessage(), streamContext);
                return;
            }
            if (sessionId != null) {
                chatHistoryPersistenceService.markSessionFailedIfPending(sessionId);
                SseEmitterContext.clear(sessionId);
            }
            sendSetupError(emitter, e);
        }
    }

    private void validateRequest(ChatRequestDTO request) {
        if (request == null || StringUtils.isBlank(request.getMessage())) {
            throw new BusinessException("Message cannot be blank.");
        }
    }

    private void validateSessionOwnership(Long sessionId) {
        ChatSession session = chatSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException("Session does not exist.");
        }
        Long userId = UserContext.getUser();
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized access to the session.");
        }
    }

    private void attachEmitterLifecycle(ChatStreamContext streamContext) {
        SseEmitter emitter = streamContext.getEmitter();
        Long sessionId = streamContext.getSessionId();
        emitter.onCompletion(() -> closeStreamContext(streamContext));
        emitter.onTimeout(() -> closeStreamContext(streamContext));
        emitter.onError(error -> closeStreamContext(streamContext));
    }

    private void handlePartialResponse(ChatStreamContext streamContext, String token) {
        if (streamContext.isClosed()) {
            return;
        }
        try {
            streamContext.appendAssistantText(token);
            sseEventPublisher.publishText(streamContext.getEmitter(), token);
        } catch (Exception e) {
            handleDisconnectedStream(streamContext.getSessionId(), streamContext, e);
        }
    }

    private void handleCompleteResponse(Long sessionId,
                                        String userMessage,
                                        ChatStreamContext streamContext,
                                        String fullAssistantReply,
                                        boolean newSession) {
        if (streamContext.isClosed()) {
            return;
        }
        String finalReply = StringUtils.blankToDefault(
                fullAssistantReply,
                streamContext.getIntroText() + "\n" + streamContext.getReasonText()
        ).trim();
        try {
            log.info("AI chat stream completed. sessionId={}, introText={}, courseCardCount={}, reasonText={}, fullAssistantReply={}",
                    sessionId,
                    sanitizeForLog(streamContext.getIntroText()),
                    streamContext.getCourseCards().size(),
                    sanitizeForLog(streamContext.getReasonText()),
                    sanitizeForLog(finalReply));
            chatHistoryPersistenceService.persistCompletedRound(
                    sessionId,
                    userMessage,
                    streamContext.getAssistantBlocks(),
                    finalReply
            );
            if (newSession) {
                // 【AI 标题生成】首轮历史落库成功后再旁路生成标题；失败由会话服务兜底，不能影响 done。
                chatSessionService.generateAndUpdateTitle(sessionId, userMessage, finalReply);
            }
            sseEventPublisher.publishDone(streamContext.getEmitter());
            streamContext.getEmitter().complete();
        } catch (Exception e) {
            handleError(sessionId, userMessage, streamContext, e);
        } finally {
            closeStreamContext(streamContext);
        }
    }

    private void handleError(Long sessionId, String userMessage, ChatStreamContext streamContext, Throwable error) {
        if (isOutOfScopeError(error)) {
            handleOutOfScopeReply(sessionId, userMessage, streamContext);
            return;
        }
        if (!streamContext.markClosed()) {
            return;
        }
        log.error("AI chat stream encountered an error. sessionId={}", sessionId, error);
        chatHistoryPersistenceService.markSessionFailedIfPending(sessionId);
        try {
            sseEventPublisher.publishError(streamContext.getEmitter(), "AI_STREAM_ERROR", resolveErrorMessage(error), true);
            streamContext.getEmitter().complete();
        } catch (Exception sendError) {
            log.error("Failed to send SSE error event. sessionId={}", sessionId, sendError);
            streamContext.getEmitter().completeWithError(error);
        } finally {
            SseEmitterContext.clear(sessionId);
        }
    }

    private void handleOutOfScopeReply(Long sessionId, String userMessage, ChatStreamContext streamContext) {
        if (!streamContext.markClosed()) {
            return;
        }
        String reply = CourseIntentQueryTransformer.OUT_OF_SCOPE_REPLY;
        try {
            streamContext.appendAssistantText(reply);
            sseEventPublisher.publishText(streamContext.getEmitter(), reply);
            chatHistoryPersistenceService.persistCompletedRound(
                    sessionId,
                    userMessage,
                    streamContext.getAssistantBlocks(),
                    reply
            );
            sseEventPublisher.publishDone(streamContext.getEmitter());
            streamContext.getEmitter().complete();
        } catch (Exception e) {
            log.error("Failed to send out-of-scope reply. sessionId={}", sessionId, e);
            chatHistoryPersistenceService.markSessionFailedIfPending(sessionId);
            try {
                streamContext.getEmitter().completeWithError(e);
            } catch (Exception completeError) {
                log.debug("SSE emitter was already completed. sessionId={}", sessionId, completeError);
            }
        } finally {
            SseEmitterContext.clear(sessionId);
        }
    }

    private void handleDisconnectedStream(Long sessionId, ChatStreamContext streamContext, Throwable error) {
        if (!streamContext.markClosed()) {
            return;
        }
        log.warn("SSE connection is no longer usable. sessionId={}", sessionId, error);
        chatHistoryPersistenceService.markSessionFailedIfPending(sessionId);
        SseEmitterContext.clear(sessionId);
        try {
            streamContext.getEmitter().completeWithError(error);
        } catch (Exception completeError) {
            log.debug("SSE emitter was already completed. sessionId={}", sessionId, completeError);
        }
    }

    private void closeStreamContext(ChatStreamContext streamContext) {
        streamContext.markClosed();
        SseEmitterContext.clear(streamContext.getSessionId());
    }

    private void sendSetupError(SseEmitter emitter, Throwable error) {
        log.error("Failed to setup chat stream", error);
        try {
            sseEventPublisher.publishError(emitter, "AI_SETUP_ERROR", resolveErrorMessage(error), false);
            emitter.complete();
        } catch (Exception sendError) {
            log.error("Failed to send setup error event", sendError);
            emitter.completeWithError(error);
        }
    }

    private String resolveErrorMessage(Throwable error) {
        if (error instanceof BusinessException) {
            return error.getMessage();
        }
        return "System busy, please try again later.";
    }

    private boolean isOutOfScopeError(Throwable error) {
        return error instanceof BusinessException
                && CourseIntentQueryTransformer.OUT_OF_SCOPE_REPLY.equals(error.getMessage());
    }

    private String sanitizeForLog(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.replace("\r", "\\r").replace("\n", "\\n");
    }
}
