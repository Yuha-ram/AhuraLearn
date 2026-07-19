package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.LearningPlanQueryDTO;
import com.ahuralearn.learning.domain.dto.LearningPlanSaveDTO;
import com.ahuralearn.learning.domain.vo.LearningPlanPageVO;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import com.ahuralearn.learning.service.LearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning/plan")
@Tag(name = "learningPlanController")
@RequiredArgsConstructor
public class LearningPlanController {
    private final LearningPlanService learningPlanService;

    @Operation(summary = "Get learning plans")
    @GetMapping
    public Result<LearningPlanPageVO> getLearningPlans(LearningPlanQueryDTO queryDTO) {
        Long userId = UserContext.getUser();
        return Result.success(learningPlanService.getLearningPlanPage(userId, queryDTO));
    }

    /**
     * Create a learning plan for the current user.
     *
     * @author GXC
     */
    @Operation(summary = "Create a learning plan")
    @PostMapping
    public Result<LearningPlanVO> createPlan(@RequestBody LearningPlanSaveDTO saveDTO) {
        return Result.success(learningPlanService.createPlan(saveDTO));
    }

    /**
     * Update a learning plan owned by the current user.
     *
     * @author GXC
     */
    @Operation(summary = "Update a learning plan")
    @PutMapping("/{id}")
    public Result<LearningPlanVO> updatePlan(@PathVariable("id") Long id,
                                             @RequestBody LearningPlanSaveDTO saveDTO) {
        LearningPlanVO plan = learningPlanService.updatePlan(id, saveDTO);
        if (plan == null) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success(plan);
    }

    @Operation(summary = "Complete or uncomplete a learning plan")
    @PatchMapping("/{id}/complete")
    public Result<LearningPlanVO> toggleComplete(@PathVariable("id") Long id) {
        Long userId = UserContext.getUser();
        LearningPlanVO plan = learningPlanService.toggleComplete(userId, id);
        if (plan == null) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success(plan);
    }

    @Operation(summary = "Delete a learning plan")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLearningPlan(@PathVariable("id") Long id) {
        Long userId = UserContext.getUser();
        boolean deleted = learningPlanService.deleteLearningPlan(userId, id);
        if (!deleted) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success();
    }
}
