package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.po.CourseChapter;
import com.ahuralearn.course.domain.vo.ChapterVO;
import com.ahuralearn.course.mapper.CourseChapterMapper;
import com.ahuralearn.course.service.ICourseChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Course Chapter Table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements ICourseChapterService {

    @Override
    public List<ChapterVO> getChaptersByCourseId(Long courseId) {
        List<CourseChapter> chapters = lambdaQuery()
                .eq(CourseChapter::getCourseId, courseId)
                .orderByAsc(CourseChapter::getSortOrder)
                .list();
        if (CollUtils.isEmpty(chapters)) // chapters not found
            return CollUtils.emptyList();

        // All fields mapped except for sections
        List<ChapterVO> voList = BeanUtils.copyList(chapters, ChapterVO.class);
        return voList;
    }
}
