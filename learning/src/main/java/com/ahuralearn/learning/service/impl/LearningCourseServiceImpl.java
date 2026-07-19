package com.ahuralearn.learning.service.impl;

import com.ahuralearn.learning.domain.dto.LearningCourseQueryDTO;
import com.ahuralearn.learning.domain.vo.LearningCoursePageVO;
import com.ahuralearn.learning.mapper.LearningCourseMapper;
import com.ahuralearn.learning.service.LearningCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningCourseServiceImpl implements LearningCourseService {
    private final LearningCourseMapper learningCourseMapper;

    @Override
    public LearningCoursePageVO getUserCourses(Long userId, LearningCourseQueryDTO query) {
        LearningCourseQueryDTO effectiveQuery = query == null ? new LearningCourseQueryDTO() : query;
        if (effectiveQuery.getStatus() == null) {
            effectiveQuery.setStatus("ALL");
        }

        LearningCoursePageVO page = new LearningCoursePageVO();
        page.setInProgressCourses(learningCourseMapper.countInProgressCourses(userId));
        page.setCourses(learningCourseMapper.selectUserCourses(userId, effectiveQuery));
        page.setCategories(learningCourseMapper.selectUserCourseCategories(userId));
        return page;
    }
}
