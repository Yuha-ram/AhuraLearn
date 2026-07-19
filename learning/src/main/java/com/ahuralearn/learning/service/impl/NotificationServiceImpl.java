package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.NotificationQueryDTO;
import com.ahuralearn.learning.domain.po.LearningPlan;
import com.ahuralearn.learning.domain.po.NotificationPO;
import com.ahuralearn.learning.domain.vo.GoalVO;
import com.ahuralearn.learning.domain.vo.NotificationPageVO;
import com.ahuralearn.learning.domain.vo.NotificationVO;
import com.ahuralearn.learning.mapper.LearningGoalMapper;
import com.ahuralearn.learning.mapper.LearningPlanMapper;
import com.ahuralearn.learning.mapper.NotificationMapper;
import com.ahuralearn.learning.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, NotificationPO> implements NotificationService {
    private static final String LEARNING_PLAN_DUE = "LEARNING_PLAN_DUE";
    private static final String WEEKLY_GOAL_DUE = "WEEKLY_GOAL_DUE";
    private static final Pattern STUDY_TIME_PATTERN = Pattern.compile("(\\d+)");

    private final LearningPlanMapper learningPlanMapper;
    private final LearningGoalMapper learningGoalMapper;

    @Override
    public NotificationPageVO getNotificationPage(NotificationQueryDTO queryDTO) {
        Long userId = UserContext.getUser();
        int pageNum = queryDTO == null || queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
        int pageSize = queryDTO == null || queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();

        Page<NotificationPO> page = page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<NotificationPO>()
                .eq(NotificationPO::getUserId, userId)
                .orderByAsc(NotificationPO::getAcknowledged)
                .orderByDesc(NotificationPO::getCreateTime)
                .orderByDesc(NotificationPO::getId));

        List<NotificationVO> records = page.getRecords()
                .stream()
                .map(this::toVO)
                .toList();

        NotificationPageVO vo = new NotificationPageVO();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setRecords(records);
        return vo;
    }

    @Override
    @Transactional
    public NotificationVO acknowledgeNotification(Long id) {
        Long userId = UserContext.getUser();
        boolean updated = update(new LambdaUpdateWrapper<NotificationPO>()
                .set(NotificationPO::getAcknowledged, true)
                .set(NotificationPO::getUpdateTime, LocalDateTime.now())
                .eq(NotificationPO::getId, id)
                .eq(NotificationPO::getUserId, userId));
        if (!updated) {
            return null;
        }
        return getCurrentUserNotification(id, userId);
    }

    @Override
    public boolean deleteNotification(Long id) {
        Long userId = UserContext.getUser();
        return remove(new LambdaQueryWrapper<NotificationPO>()
                .eq(NotificationPO::getId, id)
                .eq(NotificationPO::getUserId, userId));
    }

    private NotificationVO getCurrentUserNotification(Long id, Long userId) {
        NotificationPO notification = getOne(new LambdaQueryWrapper<NotificationPO>()
                .eq(NotificationPO::getId, id)
                .eq(NotificationPO::getUserId, userId));
        return notification == null ? null : toVO(notification);
    }

    private NotificationVO toVO(NotificationPO notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setDetails(notification.getContent());
        vo.setType(notification.getType());
        vo.setAcknowledged(notification.getAcknowledged());
        vo.setRelatedId(notification.getRelatedId());
        fillRelatedFields(vo, notification);
        vo.setCreateTime(notification.getCreateTime());
        vo.setUpdateTime(notification.getUpdateTime());
        return vo;
    }

    private void fillRelatedFields(NotificationVO vo, NotificationPO notification) {
        if (LEARNING_PLAN_DUE.equals(notification.getType())) {
            LearningPlan plan = learningPlanMapper.selectOne(new LambdaQueryWrapper<LearningPlan>()
                    .eq(LearningPlan::getId, notification.getRelatedId())
                    .eq(LearningPlan::getUserId, notification.getUserId()));
            if (plan == null) {
                return;
            }
            vo.setDueDate(plan.getDueDate());
            vo.setDaysLeft(calculateDaysLeft(plan.getDueDate()));
            vo.setPriority(plan.getPriority());
            vo.setStatus(Boolean.TRUE.equals(plan.getCompleted()) ? "Completed" : "Pending");
            vo.setDescription(resolveDescription(plan.getNote(), notification.getContent()));
            vo.setProgress(Boolean.TRUE.equals(plan.getCompleted()) ? 100 : 0);
            vo.setEstimatedMinutes(parseEstimatedMinutes(plan.getStudyTime()));
            vo.setCourseName(resolveCourseName(plan.getTitle()));
            vo.setNextSteps(resolveNextSteps(plan.getTitle(), plan.getDueDate()));
            return;
        }

        if (WEEKLY_GOAL_DUE.equals(notification.getType())) {
            GoalVO goal = learningGoalMapper.selectUserGoal(notification.getUserId(), notification.getRelatedId());
            if (goal == null) {
                return;
            }
            LocalDate dueDate = resolveCurrentWeekDueDate(goal.getDueDay());
            vo.setDueDate(dueDate);
            vo.setDueDay(goal.getDueDay());
            vo.setDaysLeft(calculateDaysLeft(dueDate));
            vo.setStatus(Boolean.TRUE.equals(goal.getAchieved()) ? "Completed" : "Pending");
            vo.setDescription(notification.getContent());
            vo.setProgress(calculateGoalProgress(goal));
            vo.setCourseName(goal.getType());
            vo.setNextSteps(List.of("Keep working toward " + goal.getTitle() + "."));
        }
    }

    private String resolveDescription(String note, String content) {
        if (note != null && !note.isBlank()) {
            return note;
        }
        return content;
    }

    private Integer parseEstimatedMinutes(String studyTime) {
        if (studyTime == null || studyTime.isBlank()) {
            return null;
        }

        Matcher matcher = STUDY_TIME_PATTERN.matcher(studyTime);
        if (!matcher.find()) {
            return null;
        }

        int amount = Integer.parseInt(matcher.group(1));
        String normalized = studyTime.toLowerCase(Locale.ENGLISH);
        if (normalized.contains("hour") || normalized.contains("hr") || normalized.contains("h")) {
            return amount * 60;
        }
        return amount;
    }

    private String resolveCourseName(String title) {
        if (title == null || title.isBlank()) {
            return "General Learning Track";
        }

        String normalized = title.toLowerCase(Locale.ENGLISH);
        if (normalized.contains("python")) {
            return "Python Track";
        }
        if (normalized.contains("spring") || normalized.contains("java")) {
            return "Java Backend Track";
        }
        if (normalized.contains("react") || normalized.contains("javascript") || normalized.contains("web")) {
            return "Web Development Track";
        }
        if (normalized.contains("data") || normalized.contains("sql")) {
            return "Data Track";
        }
        return "General Learning Track";
    }

    private List<String> resolveNextSteps(String title, LocalDate dueDate) {
        String taskTitle = title == null || title.isBlank() ? "this learning plan" : title;
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return List.of("Finish " + taskTitle + " as soon as possible.");
        }
        return List.of("Continue working on " + taskTitle + ".");
    }

    private Integer calculateGoalProgress(GoalVO goal) {
        if (goal.getCurrentValue() == null || goal.getTotalValue() == null || goal.getTotalValue() <= 0) {
            return Boolean.TRUE.equals(goal.getAchieved()) ? 100 : 0;
        }
        return Math.min(100, goal.getCurrentValue() * 100 / goal.getTotalValue());
    }

    private Long calculateDaysLeft(LocalDate dueDate) {
        if (dueDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
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
}
