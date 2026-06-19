package com.ahuralearn.course.service;

import com.ahuralearn.course.domain.po.CourseSection;
import com.ahuralearn.course.domain.vo.SectionBasicVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Course Section / Video Table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
public interface ICourseSectionService extends IService<CourseSection> {

    Map<Long, List<CourseSection>> getSectionsByCourseId(Long courseId);

    // deprecated
    SectionBasicVO getSectionSimpleInfoById(Long sectionId);

    SectionBasicVO getSectionInfoForPlayback(Long courseId, Long sectionId);
}
