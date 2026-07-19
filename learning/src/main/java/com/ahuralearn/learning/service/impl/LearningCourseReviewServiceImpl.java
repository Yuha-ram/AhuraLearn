package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.CourseReviewSubmitDTO;
import com.ahuralearn.learning.domain.vo.CourseReviewVO;
import com.ahuralearn.learning.mapper.LearningCourseReviewMapper;
import com.ahuralearn.learning.service.LearningCourseReviewService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningCourseReviewServiceImpl implements LearningCourseReviewService {
    private final LearningCourseReviewMapper reviewMapper;

    @PostConstruct
    public void initializeReviewTable() {
        reviewMapper.createTableIfNotExists();
    }

    @Override
    @Transactional
    public CourseReviewVO submitReview(Long courseId, CourseReviewSubmitDTO dto) {
        validate(courseId, dto);
        Long userId = UserContext.getUser();

        if (reviewMapper.countCompletedUserCourseLearning(userId, courseId) == 0) {
            throw new BusinessException("You can only review completed courses");
        }

        reviewMapper.upsertReview(userId, courseId, dto);
        reviewMapper.updateCourseReviewSummary(courseId);
        return reviewMapper.selectUserReview(userId, courseId);
    }

    @Override
    public List<CourseReviewVO> getReviews(Long courseId) {
        if (courseId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        return reviewMapper.selectCourseReviews(courseId);
    }

    private void validate(Long courseId, CourseReviewSubmitDTO dto) {
        if (courseId == null || dto == null || dto.getRating() == null
                || dto.getRating() < 1 || dto.getRating() > 5
                || (dto.getComment() != null && dto.getComment().length() > 500)) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
    }
}
