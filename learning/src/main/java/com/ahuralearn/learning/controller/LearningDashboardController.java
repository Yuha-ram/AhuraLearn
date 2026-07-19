package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.vo.LearningDashboardVO;
import com.ahuralearn.learning.service.LearningDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning/dashboard")
@Tag(name = "learningDashboardController")
@RequiredArgsConstructor
public class LearningDashboardController {
    private final LearningDashboardService dashboardService;

    @Operation(summary = "Get the current user's learning dashboard")
    @GetMapping
    public Result<LearningDashboardVO> getDashboard() {
        Long userId = UserContext.getUser();
        return Result.success(dashboardService.getDashboard(userId));
    }
}
