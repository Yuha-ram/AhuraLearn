package com.ahuralearn.adaptiveexam.controller;

import com.ahuralearn.adaptiveexam.domain.vo.CourseListVO;
import com.ahuralearn.adaptiveexam.service.IAssessmentCourseService;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AssessmentCourseController {

    private final IAssessmentCourseService courseService;

    //TODO 改为lesson判断用户课程注册情况，只有注册过的课程才可以测试
    @GetMapping("/api/courses")
    public List<CourseListVO> getCourses() {
        System.out.println("========== GET /api/courses ==========");
        return courseService.getAvailableCourses();
    }

    @PostConstruct
    public void init() {
        System.out.println("===== CourseController Loaded =====");
    }


}


