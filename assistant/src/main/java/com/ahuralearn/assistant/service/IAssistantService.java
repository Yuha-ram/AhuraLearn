package com.ahuralearn.assistant.service;

import com.ahuralearn.assistant.domain.dto.AnalyzeDTO;
import com.ahuralearn.assistant.domain.dto.ChatDTO;
import com.ahuralearn.assistant.domain.vo.AnalysisVO;
import com.ahuralearn.assistant.domain.vo.ChatMessageVO;

import java.util.List;

/**
 * <p>
 * Academic assistant service
 * </p>
 *
 * @author Dariush
 * @since 2026-06-18
 */
public interface IAssistantService {

    /** Answer a question grounded on a single document (chat), with persisted memory. */
    ChatMessageVO chat(ChatDTO dto);

    /** The stored tutor conversation for one document (oldest first), for the UI to restore. */
    List<ChatMessageVO> chatHistory(Long documentId);

    /** Answer a free-form query across all ready documents (analyze). */
    AnalysisVO analyze(AnalyzeDTO dto);
}
