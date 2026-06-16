package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.service.ICourseService;
import com.ahuralearn.learning.domain.po.LearningLesson;
import com.ahuralearn.learning.domain.vo.EnrollmentStatusVO;
import com.ahuralearn.learning.enums.LearningStatus;
import com.ahuralearn.learning.mapper.LearningLessonMapper;
import com.ahuralearn.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * User Course Progress Table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-16
 */
@Service
@RequiredArgsConstructor
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    private final ICourseService courseService;

    @Override
    public EnrollmentStatusVO getEnrollmentStatus(Long courseId) {
        if (courseId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();

        return lesson == null
                ? new EnrollmentStatusVO(false, null)
                : new EnrollmentStatusVO(true, lesson.getLatestSectionId());
    }

    @Override
    @Transactional
    public void enrollCourse(Long courseId) {
        if (courseId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);
        Course course = courseService.getById(courseId);
        if (course == null)
            throw new BusinessException("Course you want to enroll in not found");

        //validate whether user enrolled or not
        Long userId = UserContext.getUser();

        boolean isEnrolled = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .exists();
        if (isEnrolled)
            throw new BusinessException("You have already enrolled in this course");

        //save learning records for the course
        LearningLesson lesson = new LearningLesson();
        lesson.setCourseId(courseId).setUserId(userId).setStatus(LearningStatus.InProgress);
        save(lesson);

        //increment enrollment count for the course
        courseService.lambdaUpdate()
                .setSql("enrolled_count = enrolled_count + 1")
                .eq(Course::getId, courseId)
                .update();
    }
}
