package com.ahuralearn.course.controller;


import com.ahuralearn.common.domain.vo.PageVO;
import com.ahuralearn.course.domain.query.CoursePageQuery;
import com.ahuralearn.course.domain.vo.CourseBasicInfoVO;
import com.ahuralearn.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * Core course information table 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "courseController")
public class CourseController {

    private final ICourseService courseService;

    @Operation(summary = "Retrieve trending courses")
    @GetMapping("/trending")
    public List<CourseBasicInfoVO> getTrendingCourses() {
        return courseService.getTrendingCourses();
    }

    @Operation(summary = "Retrieve new courses")
    @GetMapping("/new")
    public List<CourseBasicInfoVO> getNewCourses() {
        return courseService.getNewCourses();
    }

    @Operation(summary = "Search courses with pagination")
    @GetMapping("/page")
    public PageVO<CourseBasicInfoVO> queryCoursePage(CoursePageQuery query) {
        return courseService.queryCoursePage(query);
    }
}
