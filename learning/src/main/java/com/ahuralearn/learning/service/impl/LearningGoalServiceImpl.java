package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.GoalDTO;
import com.ahuralearn.learning.domain.po.WeeklyGoal;
import com.ahuralearn.learning.domain.vo.GoalVO;
import com.ahuralearn.learning.mapper.LearningGoalMapper;
import com.ahuralearn.learning.service.LearningGoalService;
import com.ahuralearn.learning.service.NotificationGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LearningGoalServiceImpl implements LearningGoalService {
    private final LearningGoalMapper goalMapper;
    private final NotificationGenerateService notificationGenerateService;

    @Override
    public List<GoalVO> getGoals() {
        return goalMapper.selectUserGoals(UserContext.getUser());
    }

    @Override
    @Transactional
    public GoalVO createGoal(GoalDTO dto) {
        Long userId = UserContext.getUser();
        WeeklyGoal goal = new WeeklyGoal();
        goal.setUserId(userId);
        goal.setTitle(dto.getTitle());
        goal.setType(dto.getType());
        goal.setCurrentValue(dto.getCurrentValue());
        goal.setTotalValue(dto.getTotalValue());
        goal.setAchieved(false);
        goal.setAchievedDay(null);
        goal.setDueDay(dto.getDueDay());
        goalMapper.insertGoal(goal);
        notificationGenerateService.syncWeeklyGoalDueNotification(
                userId, goal.getId(), goal.getTitle(), goal.getAchieved(), goal.getDueDay());
        return goalMapper.selectUserGoal(userId, goal.getId());
    }

    @Override
    @Transactional
    public GoalVO updateGoal(Long id, GoalDTO dto) {
        Long userId = UserContext.getUser();
        if (goalMapper.updateGoal(userId, id, dto) == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        GoalVO goal = goalMapper.selectUserGoal(userId, id);
        notificationGenerateService.syncWeeklyGoalDueNotification(
                userId, goal.getId(), goal.getTitle(), goal.getAchieved(), goal.getDueDay());
        return goal;
    }

    @Override
    @Transactional
    public GoalVO toggleComplete(Long userId, Long id) {
        GoalVO goal = goalMapper.selectUserGoal(userId, id);
        if (goal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        boolean achieved = !Boolean.TRUE.equals(goal.getAchieved());
        int currentValue = achieved ? goal.getTotalValue() : 0;
        String achievedDay = achieved
                ? LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                : null;

        if (goalMapper.updateGoalCompletion(
                userId, id, achieved, currentValue, achievedDay) == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        GoalVO updatedGoal = goalMapper.selectUserGoal(userId, id);
        notificationGenerateService.syncWeeklyGoalDueNotification(
                userId, updatedGoal.getId(), updatedGoal.getTitle(), updatedGoal.getAchieved(), updatedGoal.getDueDay());
        return updatedGoal;
    }

    @Override
    @Transactional
    public void deleteGoal(Long id) {
        Long userId = UserContext.getUser();
        if (goalMapper.deleteGoal(userId, id) == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        notificationGenerateService.deleteWeeklyGoalDueNotification(userId, id);
    }
}
