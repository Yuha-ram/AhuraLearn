package com.ahuralearn.learning.service.impl;

import com.ahuralearn.learning.domain.po.LearningPlan;
import com.ahuralearn.learning.domain.po.NotificationPO;
import com.ahuralearn.learning.mapper.LearningGoalMapper;
import com.ahuralearn.learning.mapper.LearningPlanMapper;
import com.ahuralearn.learning.mapper.NotificationMapper;
import com.ahuralearn.learning.service.NotificationGenerateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NotificationGenerateServiceImpl implements NotificationGenerateService {
    private static final String LEARNING_PLAN_DUE = "LEARNING_PLAN_DUE";
    private static final String WEEKLY_GOAL_DUE = "WEEKLY_GOAL_DUE";

    private final NotificationMapper notificationMapper;
    private final LearningPlanMapper learningPlanMapper;
    private final LearningGoalMapper learningGoalMapper;

    @Override
    @Transactional
    public void syncLearningPlanDueNotification(LearningPlan plan) {
        if (plan == null || plan.getUserId() == null || plan.getId() == null) {
            return;
        }

        if (Boolean.TRUE.equals(plan.getCompleted()) || !isDueThisWeek(plan.getDueDate())) {
            deleteNotification(plan.getUserId(), LEARNING_PLAN_DUE, plan.getId());
            return;
        }

        String dueText = formatDueText(plan.getDueDate());
        upsertNotification(
                plan.getUserId(),
                LEARNING_PLAN_DUE,
                plan.getId(),
                plan.getTitle(),
                "Learning plan due soon. Due " + dueText + "."
        );
    }

    @Override
    @Transactional
    public void deleteLearningPlanDueNotification(Long userId, Long planId) {
        deleteNotification(userId, LEARNING_PLAN_DUE, planId);
    }

    @Override
    @Transactional
    public void syncWeeklyGoalDueNotification(Long userId, Long goalId, String title, Boolean achieved, String dueDay) {
        if (userId == null || goalId == null) {
            return;
        }

        LocalDate dueDate = resolveCurrentWeekDueDate(dueDay);
        if (Boolean.TRUE.equals(achieved) || !isDueThisWeek(dueDate)) {
            deleteNotification(userId, WEEKLY_GOAL_DUE, goalId);
            return;
        }

        upsertNotification(
                userId,
                WEEKLY_GOAL_DUE,
                goalId,
                title,
                "Weekly goal due soon. Due " + formatDueText(dueDate) + "."
        );
    }

    @Override
    @Transactional
    public void deleteWeeklyGoalDueNotification(Long userId, Long goalId) {
        deleteNotification(userId, WEEKLY_GOAL_DUE, goalId);
    }

    private void upsertNotification(Long userId, String type, Long relatedId, String title, String content) {
        LocalDateTime now = LocalDateTime.now();
        NotificationPO existing = notificationMapper.selectOne(new LambdaQueryWrapper<NotificationPO>()
                .eq(NotificationPO::getUserId, userId)
                .eq(NotificationPO::getType, type)
                .eq(NotificationPO::getRelatedId, relatedId));

        if (existing == null) {
            NotificationPO notification = new NotificationPO();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setAcknowledged(false);
            notification.setRelatedId(relatedId);
            notification.setCreateTime(now);
            notification.setUpdateTime(now);
            notificationMapper.insert(notification);
            return;
        }

        notificationMapper.update(null, new LambdaUpdateWrapper<NotificationPO>()
                .set(NotificationPO::getTitle, title)
                .set(NotificationPO::getContent, content)
                .set(NotificationPO::getUpdateTime, now)
                .eq(NotificationPO::getId, existing.getId())
                .eq(NotificationPO::getUserId, userId));
    }

    private void deleteNotification(Long userId, String type, Long relatedId) {
        if (userId == null || relatedId == null) {
            return;
        }
        notificationMapper.delete(new LambdaQueryWrapper<NotificationPO>()
                .eq(NotificationPO::getUserId, userId)
                .eq(NotificationPO::getType, type)
                .eq(NotificationPO::getRelatedId, relatedId));
    }

    private boolean isDueThisWeek(LocalDate dueDate) {
        if (dueDate == null) {
            return false;
        }
        LocalDate endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return !dueDate.isAfter(endOfWeek);
    }

    private LocalDate resolveCurrentWeekDueDate(String dueDay) {
        DayOfWeek dayOfWeek = parseDayOfWeek(dueDay);
        if (dayOfWeek == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        return startOfWeek.plusDays(dayOfWeek.getValue() - 1L);
    }

    private DayOfWeek parseDayOfWeek(String dueDay) {
        if (dueDay == null || dueDay.isBlank()) {
            return null;
        }

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            String fullName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            String shortName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            if (fullName.equalsIgnoreCase(dueDay.trim()) || shortName.equalsIgnoreCase(dueDay.trim())) {
                return dayOfWeek;
            }
        }
        return null;
    }

    private String formatDueText(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        if (dueDate.isBefore(today)) {
            return "overdue";
        }
        if (dueDate.isEqual(today)) {
            return "today";
        }
        if (dueDate.isEqual(today.plusDays(1))) {
            return "tomorrow";
        }
        return "on " + dueDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
