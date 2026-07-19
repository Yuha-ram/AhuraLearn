package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.LearningCourseQueryDTO;
import com.ahuralearn.learning.domain.vo.LearningCoursePageVO;

public interface LearningCourseService {
    LearningCoursePageVO getUserCourses(Long userId, LearningCourseQueryDTO query);
}
