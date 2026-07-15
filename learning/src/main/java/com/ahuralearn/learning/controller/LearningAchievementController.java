package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.vo.AchievementSummaryVO;
import com.ahuralearn.learning.service.LearningAchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning/achievements")
@Tag(name = "learningAchievementController")
@RequiredArgsConstructor
public class LearningAchievementController {
    private final LearningAchievementService achievementService;

    @Operation(summary = "Get the current user's achievement summary")
    @GetMapping("/summary")
    public Result<AchievementSummaryVO> getSummary() {
        Long userId = UserContext.getUser();
        return Result.success(achievementService.getSummary(userId));
    }
}
