package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.dto.CourseReviewSubmitDTO;
import com.ahuralearn.learning.domain.vo.CourseReviewVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LearningCourseReviewMapper {
    void createTableIfNotExists();

    int countCompletedUserCourseLearning(@Param("userId") Long userId, @Param("courseId") Long courseId);

    int upsertReview(@Param("userId") Long userId,
                     @Param("courseId") Long courseId,
                     @Param("dto") CourseReviewSubmitDTO dto);

    CourseReviewVO selectUserReview(@Param("userId") Long userId, @Param("courseId") Long courseId);

    List<CourseReviewVO> selectCourseReviews(@Param("courseId") Long courseId);

    int updateCourseReviewSummary(@Param("courseId") Long courseId);
}
