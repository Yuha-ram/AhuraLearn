package com.ahuralearn.adaptiveexam.service;

import com.ahuralearn.adaptiveexam.domain.vo.CourseListVO;
import java.util.List;

public interface IAssessmentCourseService {
    List<CourseListVO> getAvailableCourses();
}