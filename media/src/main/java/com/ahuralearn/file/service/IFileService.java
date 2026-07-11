package com.ahuralearn.file.service;

import com.ahuralearn.file.domain.po.File;
import com.ahuralearn.file.domain.vo.FileVO;
import com.ahuralearn.file.domain.vo.UploadVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * Uploaded file table service
 * </p>
 * Extends MyBatis-Plus {@link IService} (so generic CRUD like getById/updateById
 * is inherited) and adds the file-feature operations.
 *
 * @author Dariush
 * @since 2026-06-18
 */
public interface IFileService extends IService<File> {

    /** Store the uploaded file (OSS + DB), extract its text and build the summary. */
    UploadVO upload(MultipartFile file);

    /** List all uploaded files, newest first, as lightweight VOs. */
    List<FileVO> listFiles();

    /** Delete a file by id: the DB row and the OSS object. */
    void deleteFile(Long id);
}
