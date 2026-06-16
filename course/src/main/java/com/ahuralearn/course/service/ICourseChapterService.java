package com.ahuralearn.course.service;

import com.ahuralearn.course.domain.po.CourseChapter;
import com.ahuralearn.course.domain.vo.ChapterVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Course Chapter Table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
public interface ICourseChapterService extends IService<CourseChapter> {

    List<ChapterVO> getChaptersByCourseId(Long courseId);
}
