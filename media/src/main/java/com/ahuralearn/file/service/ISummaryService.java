package com.ahuralearn.file.service;

import com.ahuralearn.file.domain.vo.RegenerateVO;
import com.ahuralearn.file.domain.vo.SummaryVO;

/**
 * <p>
 * AI summarization service
 * </p>
 *
 * @author Dariush
 * @since 2026-06-18
 */
public interface ISummaryService {

    /** Read a file's stored summary + key points. */
    SummaryVO getSummary(Long documentId);

    /** Rebuild a file's summary + key points from its extracted text and save them. */
    RegenerateVO regenerate(Long documentId);
}
