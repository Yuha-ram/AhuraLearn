package com.ahuralearn.course.service;

import com.ahuralearn.common.domain.vo.PageVO;
import com.ahuralearn.course.domain.dto.CourseVectorDTO;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.domain.query.CoursePageQuery;
import com.ahuralearn.course.domain.vo.CourseBasicInfoVO;
import com.ahuralearn.course.domain.vo.CourseFullInfoVO;
import com.ahuralearn.course.domain.vo.CoursePlayDetailsVO;
import com.ahuralearn.course.domain.vo.CourseSyllabusVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Core course information table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
public interface ICourseService extends IService<Course> {

    List<CourseBasicInfoVO> getTrendingCourses();

    List<CourseBasicInfoVO> getNewCourses();

    PageVO<CourseBasicInfoVO> queryCoursePage(CoursePageQuery query);

    CourseFullInfoVO getCourseDetail(Long courseId);

    CourseSyllabusVO getSyllabus(Long courseId);

    CoursePlayDetailsVO getCoursePlayDetails(Long courseId, Long sectionId);

    String getPlaybackUrl(Long courseId, Long sectionId);

    List<CourseVectorDTO> getCourseMetadata();
}
