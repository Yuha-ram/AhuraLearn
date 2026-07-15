package com.ahuralearn.file.controller;

import com.ahuralearn.file.domain.vo.RegenerateVO;
import com.ahuralearn.file.domain.vo.SummaryVO;
import com.ahuralearn.file.service.ISummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * AI summarization controller
 * </p>
 * Endpoints for the AI-Summarization view: read a file's stored summary, and
 * regenerate it on demand. Returns are wrapped in the shared {@code Result} envelope.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@RestController
@RequestMapping("/api/summarization")
@Tag(name = "summarizationController")
@RequiredArgsConstructor
public class SummarizationController {

    private final ISummaryService summaryService;

    /**
     * Get the stored summary + key points for the given file id.
     */
    @Operation(summary = "Get a document summary and key points")
    @GetMapping("/{id}")
    public SummaryVO get(@PathVariable Long id) {
        return summaryService.getSummary(id);
    }

    /**
     * Rebuild the summary from the file's text; the UI then re-fetches it via GET.
     */
    @Operation(summary = "Regenerate a document summary")
    @PostMapping("/{id}/regenerate")
    public RegenerateVO regenerate(@PathVariable Long id) {
        return summaryService.regenerate(id);
    }
}
