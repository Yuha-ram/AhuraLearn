package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.GoalDTO;
import com.ahuralearn.learning.domain.vo.GoalVO;
import com.ahuralearn.learning.service.LearningGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/learning/goals")
@Tag(name = "learningGoalController")
@RequiredArgsConstructor
public class LearningGoalController {
    private final LearningGoalService goalService;

    @Operation(summary = "Get the current user's weekly goals")
    @GetMapping
    public Result<List<GoalVO>> getGoals() {
        return Result.success(goalService.getGoals());
    }

    @Operation(summary = "Create a weekly goal")
    @PostMapping
    public Result<GoalVO> createGoal(@Valid @RequestBody GoalDTO dto) {
        return Result.success(goalService.createGoal(dto));
    }

    @Operation(summary = "Update a weekly goal")
    @PutMapping("/{id}")
    public Result<GoalVO> updateGoal(@PathVariable Long id, @Valid @RequestBody GoalDTO dto) {
        return Result.success(goalService.updateGoal(id, dto));
    }

    @Operation(summary = "Complete or uncomplete a weekly goal")
    @PatchMapping("/{id}/complete")
    public Result<GoalVO> toggleComplete(@PathVariable Long id) {
        Long userId = UserContext.getUser();
        return Result.success(goalService.toggleComplete(userId, id));
    }

    @Operation(summary = "Delete a weekly goal")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return Result.success();
    }
}
