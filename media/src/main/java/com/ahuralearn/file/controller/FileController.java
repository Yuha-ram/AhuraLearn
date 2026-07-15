package com.ahuralearn.file.controller;

import com.ahuralearn.file.domain.vo.FileVO;
import com.ahuralearn.file.domain.vo.UploadVO;
import com.ahuralearn.file.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * Uploaded file table controller
 * </p>
 * REST endpoints for uploading and managing course materials. Every method
 * returns a plain VO/void which GlobalResponseAdvice wraps into the shared
 * {@code Result {code,msg,data}} envelope. Paths stay under {@code /api/documents}
 * and ids are exposed as {@code documentId} to match the existing frontend.
 *
 * @author Dariush
 * @since 2026-06-18
 */
    @RestController
    @RequestMapping("/api/documents")
    @Tag(name = "fileController")
    @RequiredArgsConstructor
    public class FileController {

    private final IFileService fileService;

    /**
     * Upload one file as multipart/form-data (form field name: "file").
     * Triggers the full pipeline: store in OSS, extract text, build the summary.
     */
    @Operation(summary = "Upload a course material")
    @PostMapping("/upload")
    public UploadVO upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }

    /**
     * List all uploaded files (newest first) so the UI can rebuild its list
     * after a page refresh.
     */
    @Operation(summary = "List uploaded documents")
    @GetMapping
    public List<FileVO> list() {
        return fileService.listFiles();
    }

    /**
     * Delete a file by id (removes the DB row and the OSS object).
     */
    @Operation(summary = "Delete an uploaded document")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        fileService.deleteFile(id);
    }
}
