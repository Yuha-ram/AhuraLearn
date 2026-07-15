package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.GoalDTO;
import com.ahuralearn.learning.domain.vo.GoalVO;

import java.util.List;

public interface LearningGoalService {
    List<GoalVO> getGoals();

    GoalVO createGoal(GoalDTO dto);

    GoalVO updateGoal(Long id, GoalDTO dto);

    GoalVO toggleComplete(Long userId, Long id);

    void deleteGoal(Long id);
}
