package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.po.CourseSection;
import com.ahuralearn.course.mapper.CourseSectionMapper;
import com.ahuralearn.course.service.ICourseSectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Course Section / Video Table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
@Service
public class CourseSectionServiceImpl extends ServiceImpl<CourseSectionMapper, CourseSection> implements ICourseSectionService {

    @Override
    public Map<Long, List<CourseSection>> getSectionsByCourseId(Long courseId) {
        List<CourseSection> sections = lambdaQuery()
                .eq(CourseSection::getCourseId, courseId)
                .orderByAsc(CourseSection::getSortOrder)
                .list();

        if (CollUtils.isEmpty(sections)) // sections not found
            return CollUtils.emptyMap();

        // grouping by chapterId
        Map<Long, List<CourseSection>> sectionMap = sections.stream()
                .collect(Collectors.groupingBy(CourseSection::getChapterId));

        return sectionMap;
    }
}
