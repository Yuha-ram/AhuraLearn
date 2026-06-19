package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.service.ICourseService;
import com.ahuralearn.learning.domain.po.LearningLesson;
import com.ahuralearn.learning.domain.po.LearningRecord;
import com.ahuralearn.learning.domain.vo.CourseLearningProgressVO;
import com.ahuralearn.learning.domain.vo.EnrollmentStatusVO;
import com.ahuralearn.learning.enums.LearningStatus;
import com.ahuralearn.learning.mapper.LearningLessonMapper;
import com.ahuralearn.learning.service.ILearningLessonService;
import com.ahuralearn.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ILearningRecordService recordService;

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

    @Override
    public CourseLearningProgressVO getPlaybackProgress(Long courseId, Long sectionId) {
        // params validation
        if (courseId == null || sectionId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 1.get lessonId to support query the rest business
        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .select(LearningLesson::getId)
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();
        if (lesson == null)
            throw new BusinessException("You haven't enrolled this course");

        Long lessonId = lesson.getId();
        // 2.get all completed section id for this course
        Set<Long> completedSecIds = recordService.getCompletedSectionIds(lessonId);

        // 3.get current section's playback moment (Defaults to 0 if no record exists)
        Integer moment = recordService.getSectionMoment(lessonId, sectionId);

        // assemble vo
        CourseLearningProgressVO vo = new CourseLearningProgressVO();
        vo.setCompletedSectionIds(completedSecIds);
        vo.setMoment(moment);
        return vo;
    }
}
