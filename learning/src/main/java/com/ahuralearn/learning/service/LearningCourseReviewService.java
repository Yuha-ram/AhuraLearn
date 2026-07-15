package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.CourseReviewSubmitDTO;
import com.ahuralearn.learning.domain.vo.CourseReviewVO;

import java.util.List;

public interface LearningCourseReviewService {
    CourseReviewVO submitReview(Long courseId, CourseReviewSubmitDTO dto);

    List<CourseReviewVO> getReviews(Long courseId);
}
