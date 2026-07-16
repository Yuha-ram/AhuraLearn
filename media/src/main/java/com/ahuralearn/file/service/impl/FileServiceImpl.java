package com.ahuralearn.file.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.file.domain.po.File;
import com.ahuralearn.file.domain.vo.FileVO;
import com.ahuralearn.file.domain.vo.UploadVO;
import com.ahuralearn.file.enums.FileStatus;
import com.ahuralearn.file.event.FileDeletedEvent;
import com.ahuralearn.file.mapper.FileMapper;
import com.ahuralearn.file.service.IFileService;
import com.ahuralearn.file.utils.FileAnalyzer;
import com.ahuralearn.media.config.cloud.AliProperties;
import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Uploaded file table service impl
 * </p>
 * Core service of the Document-Analyst feature. It owns the full upload
 * pipeline (store the binary in OSS, extract its text),
 * the listing used to repopulate the UI after a refresh, and deletion
 * (database row + the cloud object).
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    // OSS client + settings come from the common module (AliConfig / AliProperties).
    // The big binary lives in OSS; only metadata + extracted text go to the DB.
    private final OSS ossClient;
    private final AliProperties aliProperties;

    // lets other modules react to file deletions (e.g. drop the tutor chat history)
    // without this module depending on them
    private final ApplicationEventPublisher eventPublisher;

    /** hard cap on a single upload; mirrors spring.servlet.multipart.max-file-size */
    private static final long MAX_FILE_SIZE = 200L * 1024 * 1024; // 200 MB

    /** document types the feature accepts, by lower-case extension (no dot) */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "docx", "pptx"
    );

    /**
     * Handle one uploaded file end to end:
     * validate it, push the original to OSS, persist its metadata, then extract
     * its text (the summary + key points are derived later, on read).
     *
     * @param multipartFile the file sent from the browser (form field "file")
     * @return a small acknowledgement (id, name, size, status) for the frontend
     */
    @Override
    public UploadVO upload(MultipartFile multipartFile) {
        // 1. reject empty requests early with a clear business error
        if (multipartFile == null || multipartFile.isEmpty())
            throw new BusinessException("No file was uploaded");

        // 1a. enforce the 200 MB cap (defence-in-depth; the multipart config caps it too)
        if (multipartFile.getSize() > MAX_FILE_SIZE)
            throw new BusinessException("File is too large. The maximum allowed size is 200 MB");

        // 2. read the bytes once; they are reused for both OSS upload and text extraction
        String originalName = StrUtil.blankToDefault(multipartFile.getOriginalFilename(), "file");

        // 2a. accept only supported document types (checked by extension)
        String extension = suffix(originalName).replaceFirst("^\\.", "").toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension))
            throw new BusinessException("Unsupported file type \"" + extension
                    + "\". Allowed types: " + ALLOWED_EXTENSIONS);

        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new BusinessException("Failed to read the uploaded file");
        }

        // 3. store the original resource in OSS under a unique object name, and
        //    keep its public URL (objectName = "file/<uuid>.<ext>")
        String objectName = "file/" + IdUtil.fastSimpleUUID() + suffix(originalName);
        ossClient.putObject(aliProperties.getBucketName(), objectName, new ByteArrayInputStream(data));
        String fileUrl = aliProperties.getUrlPrefix() + objectName;

        // 4. build the row; create_time / update_time are filled by the DB defaults.
        //    The status is set below by the extraction outcome (the flow is synchronous,
        //    so no intermediate "processing" state is ever persisted).
        File file = new File()
                .setUserId(UserContext.getUser())
                .setOriginalName(originalName)
                .setContentType(multipartFile.getContentType())
                .setFileSize(multipartFile.getSize())
                .setFileUrl(fileUrl);

        // 5. extract the text. If extraction fails, the original file is still
        //    safely stored, so we only mark the row FAILED instead of aborting the
        //    whole upload. The summary + key points are derived later, on read.
        try {
            String text = FileAnalyzer.extractText(data, originalName, multipartFile.getContentType());
            file.setExtractedText(text)
                    .setStatus(FileStatus.UPLOADED);
        } catch (Exception e) {
            log.error("Failed to process uploaded file: {}", originalName, e);
            file.setStatus(FileStatus.FAILED);
        }

        // 6. persist and return the acknowledgement
        save(file);
        return new UploadVO(file.getId(), file.getOriginalName(), file.getFileSize(), file.getStatus());
    }

    /**
     * List every uploaded file, newest first, as lightweight VOs (the heavy
     * extracted text is left out). Used by the UI to rebuild the list on load.
     */
    @Override
    public List<FileVO> listFiles() {
        // scope to the caller (from the JWT) so a user only ever sees their own files
        List<File> files = lambdaQuery()
                .eq(File::getUserId, UserContext.getUser())
                .orderByDesc(File::getCreateTime)
                .list();
        // summary is derived from the extracted text on read (no longer stored)
        List<FileVO> vos = new ArrayList<>(files.size());
        for (File file : files) {
            FileVO vo = BeanUtils.copyBean(file, FileVO.class);
            vo.setSummary(FileAnalyzer.summarize(file.getExtractedText()));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * Delete a file: remove the database row first, then best-effort delete the
     * matching object from OSS so it does not reappear after a refresh.
     */
    @Override
    public void deleteFile(Long id) {
        // fetch scoped to the owner: a file belonging to another user looks "not found"
        File file = lambdaQuery()
                .eq(File::getId, id)
                .eq(File::getUserId, UserContext.getUser())
                .one();
        if (file == null)
            throw new BusinessException(ResultCode.NOT_FOUND);
        removeById(id);

        // notify other modules so data keyed to this document is cleaned up too
        // (the assistant module deletes the stored tutor chat history)
        eventPublisher.publishEvent(new FileDeletedEvent(id, file.getUserId()));

        // strip the public prefix back to the object name, then drop it from OSS
        String prefix = aliProperties.getUrlPrefix();
        if (StrUtil.isNotBlank(file.getFileUrl()) && file.getFileUrl().startsWith(prefix))
            ossClient.deleteObject(aliProperties.getBucketName(), file.getFileUrl().substring(prefix.length()));
    }

    /**
     * Return the file extension (including the dot), e.g. ".pdf", or "" if none,
     * so the OSS object keeps a sensible suffix.
     */
    private String suffix(String filename) {
        int dot = filename == null ? -1 : filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
