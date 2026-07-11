package com.ahuralearn.file.utils;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Local document text extraction and lightweight text helpers.
 * <p>
 * {@link #extractText} pulls plain text out of an upload (PDF via PDFBox, others
 * as UTF-8). {@link #summarize} and {@link #keyPoints} are simple extractive
 * heuristics still used for the cheap document-list preview and to split text —
 * now the LLM's output — into bullet points. The real summaries and answers are
 * produced by Qwen (DashScope) in the services (see SummaryServiceImpl /
 * AssistantServiceImpl), not here.
 *
 * @author Dariush
 * @since 2026-06-18
 */
public class FileAnalyzer {

    // utility class - never instantiated
    private FileAnalyzer() {
    }

    /**
     * Extract plain text from the uploaded bytes. PDF files go through PDFBox;
     * everything else (txt, md, ...) is read as UTF-8 text.
     *
     * @return the extracted text, or an empty string when there are no bytes
     */
    public static String extractText(byte[] data, String filename, String contentType) {
        if (data == null || data.length == 0) {
            return "";
        }
        try {
            if (isPdf(filename, contentType)) {
                // try-with-resources closes the PDF even if text stripping fails
                try (PDDocument pdf = Loader.loadPDF(data)) {
                    return new PDFTextStripper().getText(pdf).trim();
                }
            }
            return new String(data, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR.getValue(),
                    "Failed to read document content: " + e.getMessage());
        }
    }

    /**
     * Build a short extractive summary from the leading sentences of the text.
     * The lead of a document usually states what it is about, so the first
     * sentences (capped at ~600 characters) make a reasonable summary.
     */
    public static String summarize(String text) {
        List<String> sentences = sentences(text);
        if (sentences.isEmpty()) {
            return "No readable content was found in this document.";
        }
        StringBuilder sb = new StringBuilder();
        for (String sentence : sentences) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(sentence);
            // stop once we have enough text for a short summary
            if (sb.length() >= 600) {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Pick up to {@code max} key points: the first sentences long enough to
     * carry real information (very short lines are usually headings / noise).
     */
    public static List<String> keyPoints(String text, int max) {
        List<String> points = new ArrayList<>();
        for (String sentence : sentences(text)) {
            if (sentence.length() >= 40) {
                points.add(sentence);
            }
            if (points.size() >= max) {
                break;
            }
        }
        // fall back to the summary if nothing met the length threshold
        if (points.isEmpty() && !text.isBlank()) {
            points.add(summarize(text));
        }
        return points;
    }

    // true when the upload looks like a PDF, by content type or .pdf extension
    private static boolean isPdf(String filename, String contentType) {
        if (contentType != null && contentType.toLowerCase().contains("pdf")) {
            return true;
        }
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }

    // split text into sentences: collapse whitespace, then break after . ! or ?
    private static List<String> sentences(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        for (String part : normalized.split("(?<=[.!?])\\s+")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
