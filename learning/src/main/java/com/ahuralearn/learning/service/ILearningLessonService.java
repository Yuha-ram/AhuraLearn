package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.po.LearningLesson;
import com.ahuralearn.learning.domain.vo.EnrollmentStatusVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * User Course Progress Table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-16
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    EnrollmentStatusVO getEnrollmentStatus(Long courseId);

    void enrollCourse(Long courseId);
}
