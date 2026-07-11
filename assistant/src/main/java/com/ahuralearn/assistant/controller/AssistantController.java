package com.ahuralearn.assistant.controller;

import com.ahuralearn.assistant.domain.dto.AnalyzeDTO;
import com.ahuralearn.assistant.domain.dto.ChatDTO;
import com.ahuralearn.assistant.domain.vo.AnalysisVO;
import com.ahuralearn.assistant.domain.vo.ChatMessageVO;
import com.ahuralearn.assistant.service.IAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Academic assistant controller
 * </p>
 * Endpoints for the Academic-Assistant feature: chat about one document, and
 * analyze a free-form query across all documents. Request bodies are validated
 * with {@code @Validated}; returns are wrapped in the shared {@code Result} envelope.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@RestController
@RequestMapping("/api/assistant")
@Tag(name = "assistantController")
@RequiredArgsConstructor
public class AssistantController {

    private final IAssistantService assistantService;

    /**
     * Ask a question about a document (body: message + optional documentId).
     */
    @Operation(summary = "Ask the assistant about a document")
    @PostMapping("/chat")
    public ChatMessageVO chat(@RequestBody @Validated ChatDTO dto) {
        return assistantService.chat(dto);
    }

    /**
     * The stored tutor conversation for one document (oldest first), so the UI can
     * restore the chat after a page refresh.
     */
    @Operation(summary = "Get the tutor chat history for a document")
    @GetMapping("/chat/history")
    public List<ChatMessageVO> chatHistory(@RequestParam(required = false) Long documentId) {
        return assistantService.chatHistory(documentId);
    }

    /**
     * Analyze a free-form academic query (body: query) across the ready documents.
     */
    @Operation(summary = "Analyze a free-form academic query")
    @PostMapping("/analyze")
    public AnalysisVO analyze(@RequestBody @Validated AnalyzeDTO dto) {
        return assistantService.analyze(dto);
    }
}
