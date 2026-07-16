package com.ahuralearn.adaptiveexam.service.impl;

import com.ahuralearn.adaptiveexam.domain.vo.CourseListVO;
import com.ahuralearn.adaptiveexam.mapper.AssessmentCourseMapper;
import com.ahuralearn.adaptiveexam.service.IAssessmentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentCourseServiceImpl implements IAssessmentCourseService {

    private final AssessmentCourseMapper courseMapper;

    @Override
    public List<CourseListVO> getAvailableCourses() {

        // 获取当前登录用户ID
        Long currentUserId = com.ahuralearn.common.utils.UserContext.getUser(); 

        // 1. 直接查询该用户选修的课程
        List<CourseListVO> courses = courseMapper.getEnrolledCourses(currentUserId);

        if (courses == null) {
            return new ArrayList<>();
        }

        // 2. 格式化结果，确保 id 也是 String 类型以便前端匹配
        List<CourseListVO> result = new ArrayList<>();
        for (CourseListVO course : courses) {
            CourseListVO vo = new CourseListVO();
            vo.setId(course.getId());
            vo.setValue(String.valueOf(course.getId()));
            vo.setName(course.getName());
            vo.setLabel(course.getName());
            result.add(vo);
        }

        return result;
    }
}