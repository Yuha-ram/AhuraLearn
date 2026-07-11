package com.ahuralearn.assistant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ahuralearn.assistant.domain.dto.AnalyzeDTO;
import com.ahuralearn.assistant.domain.dto.ChatDTO;
import com.ahuralearn.assistant.domain.po.ChatMessage;
import com.ahuralearn.assistant.domain.vo.AnalysisVO;
import com.ahuralearn.assistant.domain.vo.ChatMessageVO;
import com.ahuralearn.assistant.domain.vo.SourceVO;
import com.ahuralearn.assistant.service.IAssistantService;
import com.ahuralearn.assistant.service.IChatMessageService;
import com.ahuralearn.assistant.util.AcademicSources;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.file.domain.po.File;
import com.ahuralearn.file.service.IFileService;
import com.ahuralearn.file.utils.FileAnalyzer;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Academic assistant service impl
 * </p>
 * Two distinct features, both answered by Qwen (Alibaba DashScope) via LangChain4j:
 * <ul>
 *   <li>{@code chat} — the AI tutor: answers grounded on ONE selected uploaded document
 *       (its text is passed into the prompt). The conversation is persisted in the
 *       {@code assistant_chat_message} table: the recent history is replayed into each prompt so the
 *       tutor remembers the conversation, and {@code chatHistory} reloads it for the UI.</li>
 *   <li>{@code analyze} — the general research assistant: answers a conceptual question or
 *       analyzes pasted text from the model's general knowledge; it is NOT tied to the
 *       uploaded course materials, and cites credible external references (asked of the
 *       model) as verification sources rather than the user's files.</li>
 * </ul>
 * Analyze results are computed fresh on every call and never stored; only the tutor
 * conversation ({@code chat}) is persisted.
 * <p>
 * No RAG: text is passed directly into the prompt (document text capped at
 * {@link #MAX_INPUT_CHARS}); there is no embedding model, vector store or retrieval.
 * <p>
 * Lives in its own module and reads documents through the {@code file} module's
 * {@link IFileService} + {@link FileAnalyzer} (this module depends on file).
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements IAssistantService {

    // Upper bound on the document text passed into a single prompt, so one request
    // never sends an unbounded amount of text to the model.
    private static final int MAX_INPUT_CHARS = 20000;

    // How much conversation memory the tutor gets: the last N stored messages are
    // replayed into the prompt. The UI reloads a longer window on page load.
    private static final int PROMPT_HISTORY_MESSAGES = 10;
    private static final int UI_HISTORY_MESSAGES = 50;

    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";

    // injected from the file module — used to read File entities + their text
    private final IFileService fileService;

    // persisted tutor conversation (assistant_chat_message table)
    private final IChatMessageService chatMessageService;

    // Qwen/DashScope chat model (LangChain4j). Resolved lazily so the app boots
    // even when no API key is configured; a missing key only fails on first use.
    private final ObjectProvider<ChatModel> chatModelProvider;

    /**
     * Chat: answer a question grounded on ONE document's extracted text, with memory —
     * the recent stored conversation is replayed into the prompt, so follow-ups like
     * "explain that more" work. Both sides of the exchange are persisted afterwards.
     */
    @Override
    public ChatMessageVO chat(ChatDTO dto) {
        Long documentId = dto.getDocumentId();
        Long userId = UserContext.getUser();
        String history = historyBlock(userId, documentId);
        String prompt;
        if (documentId != null) {
            // scoped to the caller (from the JWT): another user's document looks "not found"
            File file = fileService.lambdaQuery()
                    .eq(File::getId, documentId)
                    .eq(File::getUserId, userId)
                    .one();
            if (file == null)
                throw new BusinessException(ResultCode.NOT_FOUND);
            // ground the answer on this one document by passing its text into the prompt
            prompt = """
                    You are an academic study assistant. Answer the user's question using the document below
                    and the conversation so far. If the document does not contain the answer, say so briefly
                    instead of guessing.
                    Reply in plain, natural prose — no Markdown (#, *, **, backticks, tables) and no LaTeX.

                    Document "%s":
                    %s
                    %s
                    Question: %s""".formatted(file.getOriginalName(), clip(file.getExtractedText()), history, dto.getMessage());
        } else {
            // no document selected — answer the question on its own (still with memory)
            prompt = """
                    You are an academic study assistant. Answer the user's question clearly and concisely,
                    taking the conversation so far into account.
                    %s
                    Question: %s""".formatted(history, dto.getMessage());
        }
        String reply = ask(prompt);

        // persist both sides of the exchange (only after a successful model reply, so a
        // failed request leaves no half-recorded turn)
        chatMessageService.saveBatch(List.of(
                new ChatMessage().setUserId(userId).setDocumentId(documentId)
                        .setRole(ROLE_USER).setContent(dto.getMessage()),
                new ChatMessage().setUserId(userId).setDocumentId(documentId)
                        .setRole(ROLE_ASSISTANT).setContent(reply)));

        return new ChatMessageVO(ROLE_ASSISTANT, reply, documentId);
    }

    /**
     * The stored tutor conversation for one document (oldest first), so the UI can
     * restore the chat after a page refresh. Scoped to the caller from the JWT.
     */
    @Override
    public List<ChatMessageVO> chatHistory(Long documentId) {
        List<ChatMessageVO> vos = new ArrayList<>();
        for (ChatMessage m : recentMessages(UserContext.getUser(), documentId, UI_HISTORY_MESSAGES))
            vos.add(new ChatMessageVO(m.getRole(), m.getContent(), m.getDocumentId()));
        return vos;
    }

    /**
     * Load the caller's last {@code limit} messages for a document, oldest first.
     * (Fetched newest-first with a LIMIT, then reversed back into reading order.)
     */
    private List<ChatMessage> recentMessages(Long userId, Long documentId, int limit) {
        List<ChatMessage> messages = chatMessageService.lambdaQuery()
                .eq(ChatMessage::getUserId, userId)
                .eq(documentId != null, ChatMessage::getDocumentId, documentId)
                .isNull(documentId == null, ChatMessage::getDocumentId)
                .orderByDesc(ChatMessage::getId)
                .last("LIMIT " + limit)
                .list();
        Collections.reverse(messages);
        return messages;
    }

    /**
     * Render the recent conversation as a prompt section ("Conversation so far: …"),
     * or an empty string when this is the first message.
     */
    private String historyBlock(Long userId, Long documentId) {
        List<ChatMessage> messages = recentMessages(userId, documentId, PROMPT_HISTORY_MESSAGES);
        if (messages.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder("\nConversation so far:\n");
        for (ChatMessage m : messages) {
            sb.append(ROLE_USER.equals(m.getRole()) ? "User: " : "Assistant: ")
                    .append(m.getContent()).append('\n');
        }
        return sb.toString();
    }

    /**
     * Analyze: answer a free-form conceptual question — or analyze text the user pasted —
     * using the model's general knowledge. This is a GENERAL academic assistant: it is
     * deliberately NOT grounded on the user's uploaded course materials (that is the
     * Document Analyst / Summarization feature); its verification sources are real papers
     * looked up from Crossref via {@link AcademicSources}.
     */
    @Override
    public AnalysisVO analyze(AnalyzeDTO dto) {
        String query = dto.getQuery();
        if (StrUtil.isBlank(query))
            throw new BusinessException("Please enter a question to analyze.");

        // Answer from the model's general knowledge (not the uploaded documents).
        String prompt = """
                You are an academic research assistant. Answer the user's question — or analyze the text
                they pasted — with a clear, accurate, well-structured explanation from your general knowledge.
                Reply in plain, natural prose — no Markdown (#, *, **, backticks, tables) and no LaTeX.

                Question: %s""".formatted(query);
        String explanation = ask(prompt);

        // Tool output (e.g. a generated citation) is a formatted block, not a research
        // analysis — return it as-is, with no breakdown and no source lookup.
        if (dto.isPlain())
            return new AnalysisVO(query, null, explanation, List.of(), List.of());

        List<String> keyPoints = FileAnalyzer.keyPoints(explanation, 3);

        // A short / conversational reply (e.g. a greeting like "hello") is not an analysis:
        // don't force the definition / key-points structure onto it, and skip the source
        // lookup (searching Crossref for "hello" is meaningless). Just return the message.
        if (keyPoints.size() < 2)
            return new AnalysisVO(query, null, explanation, List.of(), List.of());

        // A real analysis: expose the structured fields + real Crossref references (best-effort),
        // so the sources are genuine credible papers rather than files or invented citations.
        String definition = keyPoints.get(0);
        List<SourceVO> sources = AcademicSources.search(query, 4);
        return new AnalysisVO(query, definition, explanation, keyPoints, sources);
    }

    /**
     * Send one prompt to Qwen (DashScope) and return its reply. The model bean is
     * resolved here (lazily) so a missing/invalid API key surfaces as a clean
     * business error on call instead of crashing the application at startup.
     */
    private String ask(String prompt) {
        try {
            return chatModelProvider.getObject().chat(prompt);
        } catch (Exception e) {
            log.error("Qwen (DashScope) request failed", e);
            throw new BusinessException("The AI assistant is unavailable right now. "
                    + "Check the DashScope API key (langchain4j.community.dashscope.chat-model.api-key) and try again.");
        }
    }

    /** Cap document text so a single prompt never exceeds {@link #MAX_INPUT_CHARS} characters. */
    private String clip(String text) {
        if (text == null)
            return "";
        return text.length() <= MAX_INPUT_CHARS ? text : text.substring(0, MAX_INPUT_CHARS);
    }
}
