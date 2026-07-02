package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.po.ChatSession;
import com.ahuralearn.ai.enums.SessionStatus;
import com.ahuralearn.ai.mapper.ChatSessionMapper;
import com.ahuralearn.ai.service.IChatSessionService;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Master table storing chat session metadata for UI sidebar 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Service
@Slf4j
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements IChatSessionService {

    private static final int TEMP_TITLE_MAX_LENGTH = 20;
    private static final int TITLE_CONTEXT_MAX_LENGTH = 1200;
    private static final int TITLE_MAX_LENGTH = 80;
    private static final String TITLE_PREFIX_PATTERN = "(?i)^(title|session title|chat title)\\s*[:：-]\\s*";
    private static final String CHINESE_TEXT_PATTERN = ".*[\\u4e00-\\u9fff].*";
    private static final String ENGLISH_LETTER_PATTERN = ".*[A-Za-z].*";
    private static final String ENGLISH_TITLE_PATTERN = "[A-Za-z0-9+#\\- ]+";

    @Resource
    private ChatModel qwenChatModel;

    @Override
    public Long createNewSession(String firstMessage) {
        ChatSession session = new ChatSession();
        session.setUserId(UserContext.getUser());
        // 修改：新会话先标记为 pending，便于流式失败时保留可识别状态。
        session.setStatus(SessionStatus.PENDING);
        // 【临时标题】新会话必须快速返回 session_id，所以这里只做轻量截断，正式英文标题在首轮完成后旁路生成。
        String title = firstMessage.length() > TEMP_TITLE_MAX_LENGTH
                ? firstMessage.substring(0, TEMP_TITLE_MAX_LENGTH) + "..."
                : firstMessage;
        session.setTitle(title);

        save(session);
        return session.getId(); // return the new session id
    }

    @Override
    public void updateSessionTime(Long sessionId) {
        ChatSession update = new ChatSession();
        update.setId(sessionId);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public void generateAndUpdateTitle(Long sessionId, String userMessage, String assistantReply) {
        // 【AI 标题生成】标题是旁路增强能力，任何失败都不能影响 SSE 主流程、done 事件或会话状态。
        try {
            if (sessionId == null || StringUtils.isBlank(userMessage)) {
                return;
            }

            String prompt = buildTitlePrompt(userMessage, assistantReply);
            String rawTitle = qwenChatModel.chat(prompt);
            String title = sanitizeGeneratedTitle(rawTitle);
            if (StringUtils.isBlank(title)) {
                log.warn("AI session title generation returned invalid title. sessionId={}, rawTitle={}",
                        sessionId, sanitizeForLog(rawTitle));
                return;
            }

            updateSessionTitle(sessionId, title);
            log.info("AI session title updated. sessionId={}, title={}", sessionId, title);
        } catch (Exception e) {
            log.warn("Failed to generate AI session title. sessionId={}", sessionId, e);
        }
    }

    @Override
    public void updateSessionTitle(Long sessionId, String title) {
        if (sessionId == null || StringUtils.isBlank(title)) {
            return;
        }
        ChatSession update = new ChatSession();
        update.setId(sessionId);
        update.setTitle(title);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public void updateSessionStatus(Long sessionId, SessionStatus status) {
        ChatSession update = new ChatSession();
        update.setId(sessionId);
        update.setStatus(status);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public List<ChatSession> getHistorySessions() {
        Long userId = UserContext.getUser();
        return lambdaQuery()
                .eq(ChatSession::getUserId, userId)
                .in(ChatSession::getStatus, SessionStatus.ACTIVE, SessionStatus.FAILED)
                .orderByDesc(ChatSession::getUpdateTime) // the latest session will be first
                .list();
    }

    private String buildTitlePrompt(String userMessage, String assistantReply) {
        return "You are generating a sidebar title for an AI course recommendation chat.\n\n" +
                "Requirements:\n" +
                "- Generate one concise English title.\n" +
                "- Use 3 to 8 English words.\n" +
                "- Focus on the user's learning goal, course topic, or recommendation intent.\n" +
                "- Output the title only.\n" +
                "- Do not output Chinese.\n" +
                "- Do not output quotes, Markdown, punctuation, or explanation.\n\n" +
                "User message:\n" +
                limitContext(userMessage) +
                "\n\nAssistant reply:\n" +
                limitContext(assistantReply);
    }

    private String sanitizeGeneratedTitle(String rawTitle) {
        if (StringUtils.isBlank(rawTitle)) {
            return null;
        }

        String title = rawTitle.trim()
                .replace("\r", "\n")
                .split("\n")[0]
                .replaceAll(TITLE_PREFIX_PATTERN, "")
                .replaceAll("[`*_#>\\[\\]]", "")
                .replaceAll("^[\"'“”‘’]+|[\"'“”‘’]+$", "")
                .replaceAll("[。！？.!?:;；，,]+$", "")
                .trim();

        title = title.replaceAll("\\s+", " ");
        if (title.length() > TITLE_MAX_LENGTH) {
            title = title.substring(0, TITLE_MAX_LENGTH).trim();
        }
        if (!isValidGeneratedTitle(title)) {
            return null;
        }
        return title;
    }

    private boolean isValidGeneratedTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return false;
        }
        if (title.matches(CHINESE_TEXT_PATTERN)) {
            return false;
        }
        if (!title.matches(ENGLISH_LETTER_PATTERN)) {
            return false;
        }
        if (!title.matches(ENGLISH_TITLE_PATTERN)) {
            return false;
        }
        int wordCount = title.split("\\s+").length;
        return wordCount >= 3 && wordCount <= 8;
    }

    private String limitContext(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String normalized = text.trim();
        if (normalized.length() <= TITLE_CONTEXT_MAX_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, TITLE_CONTEXT_MAX_LENGTH);
    }

    private String sanitizeForLog(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.replace("\r", "\\r").replace("\n", "\\n");
    }
}
