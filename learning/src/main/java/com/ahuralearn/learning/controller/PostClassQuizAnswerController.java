package com.ahuralearn.learning.controller;


import com.ahuralearn.learning.domain.dto.QuizAnswerDTO;
import com.ahuralearn.learning.domain.vo.QuizDisplayVO;
import com.ahuralearn.learning.domain.vo.QuizOverviewVO;
import com.ahuralearn.learning.service.IPostClassQuizAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * User quiz answer records table 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
@RestController
@RequestMapping("/section-quizzes")
@RequiredArgsConstructor
@Tag(name = "Post-Class Quiz Controller")
public class PostClassQuizAnswerController {

    private final IPostClassQuizAnswerService quizService;

    @Operation(summary = "Retrieve questions and user's answers")
    @GetMapping("/{sectionId}")
    public List<QuizDisplayVO> getQuizDetails(@PathVariable("sectionId") Long sectionId) {
        return quizService.getQuizDetails(sectionId);
    }

    @Operation(summary = "Submit quiz answers")
    @PostMapping("/{sectionId}/submit")
    public void submitQuiz(
            @PathVariable("sectionId") Long sectionId,
            @RequestBody @Validated List<QuizAnswerDTO> answers) {

        quizService.submitQuiz(sectionId, answers);
    }

    @Operation(summary = "Get quiz status and score overview")
    @GetMapping("/{sectionId}/overview")
    public QuizOverviewVO getQuizOverview(@PathVariable("sectionId") Long sectionId) {
        return quizService.getQuizOverview(sectionId);
    }
}
