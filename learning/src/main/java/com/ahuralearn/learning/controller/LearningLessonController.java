package com.ahuralearn.learning.controller;


import com.ahuralearn.learning.domain.vo.EnrollmentStatusVO;
import com.ahuralearn.learning.service.ILearningLessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * User Course Progress Table 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-16
 */
@RestController
@RequestMapping("/lessons")
@Tag(name = "learningLessonController")
@RequiredArgsConstructor
public class LearningLessonController {

    private final ILearningLessonService lessonService;

    @Operation(summary = "Get course enrollment status")
    @GetMapping("/{courseId}/enrollment")
    public EnrollmentStatusVO getEnrollmentStatus(@PathVariable("courseId") Long courseId) {
        return lessonService.getEnrollmentStatus(courseId);
    }

    @Operation(summary = "Enroll in a course")
    @PostMapping("/{courseId}/enrollment")
    public void enrollCourse(@PathVariable("courseId") Long courseId) {
        lessonService.enrollCourse(courseId);
    }
}
