package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.CourseReviewSubmitDTO;
import com.ahuralearn.learning.domain.dto.LearningCourseQueryDTO;
import com.ahuralearn.learning.domain.vo.CourseReviewVO;
import com.ahuralearn.learning.domain.vo.LearningCoursePageVO;
import com.ahuralearn.learning.service.LearningCourseReviewService;
import com.ahuralearn.learning.service.LearningCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning/courses")
@Tag(name = "learningCourseController")
@RequiredArgsConstructor
public class LearningCourseController {
    private final LearningCourseService learningCourseService;
    private final LearningCourseReviewService learningCourseReviewService;

    @Operation(summary = "Get courses learned by the current user")
    @GetMapping
    public Result<LearningCoursePageVO> getCourses(LearningCourseQueryDTO query) {
        Long userId = UserContext.getUser();
        return Result.success(learningCourseService.getUserCourses(userId, query));
    }

    @Operation(summary = "Submit or update a course review")
    @PostMapping("/{courseId}/reviews")
    public Result<CourseReviewVO> submitReview(@PathVariable Long courseId,
                                                @Valid @RequestBody CourseReviewSubmitDTO dto) {
        return Result.success(learningCourseReviewService.submitReview(courseId, dto));
    }

    @Operation(summary = "Get all reviews for a course")
    @GetMapping("/{courseId}/reviews")
    public Result<java.util.List<CourseReviewVO>> getReviews(@PathVariable Long courseId) {
        return Result.success(learningCourseReviewService.getReviews(courseId));
    }
}
