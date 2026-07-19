package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.dto.LearningPlanQueryDTO;
import com.ahuralearn.learning.domain.dto.LearningPlanSaveDTO;
import com.ahuralearn.learning.domain.po.LearningPlan;
import com.ahuralearn.learning.domain.vo.LearningPlanPageVO;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import com.ahuralearn.learning.mapper.LearningPlanMapper;
import com.ahuralearn.learning.service.LearningPlanService;
import com.ahuralearn.learning.service.NotificationGenerateService;
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
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPlanServiceImpl extends ServiceImpl<LearningPlanMapper, LearningPlan> implements LearningPlanService {
    private final NotificationGenerateService notificationGenerateService;

    /**
     * Create a learning plan for the current user.
     *
     * @author GXC
     */
    @Override
    @Transactional
    public LearningPlanVO createPlan(LearningPlanSaveDTO saveDTO) {
        return createPlanInternal(saveDTO, false);
    }

    @Override
    @Transactional
    public LearningPlanVO createAiPlan(LearningPlanSaveDTO saveDTO) {
        return createPlanInternal(saveDTO, true);
    }

    private LearningPlanVO createPlanInternal(LearningPlanSaveDTO saveDTO, boolean aiGenerated) {
        Long userId = UserContext.getUser();
        LocalDateTime now = LocalDateTime.now();

        LearningPlan plan = new LearningPlan();
        plan.setUserId(userId);
        copySaveFields(saveDTO, plan);
        plan.setCompleted(false);
        plan.setAiGenerated(aiGenerated);
        plan.setSubtitle(aiGenerated ? "AI Generated" : "Manual Entry");
        plan.setCreateTime(now);
        plan.setUpdateTime(now);
        save(plan);
        notificationGenerateService.syncLearningPlanDueNotification(plan);
        return toVO(plan);
    }

    /**
     * Update a learning plan owned by the current user.
     *
     * @author GXC
     */
    @Override
    @Transactional
    public LearningPlanVO updatePlan(Long id, LearningPlanSaveDTO saveDTO) {
        Long userId = UserContext.getUser();
        LearningPlan plan = getOne(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));
        if (plan == null) {
            return null;
        }

        copySaveFields(saveDTO, plan);
        plan.setUpdateTime(LocalDateTime.now());
        boolean updated = update(plan, new LambdaUpdateWrapper<LearningPlan>()
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));
        if (updated) {
            notificationGenerateService.syncLearningPlanDueNotification(plan);
        }
        return updated ? toVO(plan) : null;
    }

    @Override
    public LearningPlanPageVO getLearningPlanPage(Long userId, LearningPlanQueryDTO queryDTO) {
        int pageNum = queryDTO == null || queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
        int pageSize = queryDTO == null || queryDTO.getPageSize() == null ? 3 : queryDTO.getPageSize();

        Page<LearningPlan> page = lambdaQuery()
                .eq(LearningPlan::getUserId, userId)
                .orderByDesc(LearningPlan::getCreateTime)
                .page(new Page<>(pageNum, pageSize));//
//了解知识，通过Stream流处理，后用方法引用一个一个改为vo，在装入list
        List<LearningPlanVO> records = page.getRecords()
                .stream()
                .map(this::toVO)
                .toList();
//需要保留在当前方法返回类型为 LearningPlanPageVO 的情况下也需要保留，它负责组装分页响应。
        LearningPlanPageVO vo = new LearningPlanPageVO();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setRecords(records);
        return vo;
    }

    @Override
    @Transactional
    public LearningPlanVO toggleComplete(Long userId, Long id) {
        LearningPlan plan = getOne(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));

        if (plan == null) {
            return null;
        }

        boolean completed = !Boolean.TRUE.equals(plan.getCompleted());
        LocalDateTime updateTime = LocalDateTime.now();

        boolean updated = update(new LambdaUpdateWrapper<LearningPlan>()
                .set(LearningPlan::getCompleted, completed)
                .set(LearningPlan::getUpdateTime, updateTime)
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));

        if (!updated) {
            return null;
        }

        LearningPlan updatedPlan = getOne(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));
        if (updatedPlan != null) {
            notificationGenerateService.syncLearningPlanDueNotification(updatedPlan);
        }
        return updatedPlan == null ? null : toVO(updatedPlan);
    }

    @Override
    public boolean deleteLearningPlan(Long userId, Long id) {
        boolean removed = remove(new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getId, id)
                .eq(LearningPlan::getUserId, userId));
        if (removed) {
            notificationGenerateService.deleteLearningPlanDueNotification(userId, id);
        }
        return removed;
    }

    private void copySaveFields(LearningPlanSaveDTO saveDTO, LearningPlan plan) {
        plan.setTitle(saveDTO.getTitle());
        plan.setStudyTime(saveDTO.getStudyTime());
        plan.setPriority(saveDTO.getPriority());
        plan.setDueText(saveDTO.getDueText());
        plan.setDueDate(resolveDueDate(saveDTO, plan.getDueDate()));
        plan.setNote(saveDTO.getNote());
    }

    private LocalDate resolveDueDate(LearningPlanSaveDTO saveDTO, LocalDate existingDueDate) {
        if (saveDTO.getDueDate() != null) {
            return saveDTO.getDueDate();
        }

        String dueText = saveDTO.getDueText();
        if (dueText == null) {
            return existingDueDate;
        }

        LocalDate today = LocalDate.now();
        return switch (dueText.trim().toLowerCase()) {
            case "today" -> today;
            case "tomorrow" -> today.plusDays(1);
            case "this week" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case "finished" -> existingDueDate;
            default -> existingDueDate;
        };
    }

    private String calculateDueText(LearningPlan plan) {
        if (Boolean.TRUE.equals(plan.getCompleted())) {
            return "Finished";
        }

        LocalDate dueDate = plan.getDueDate();
        if (dueDate == null) {
            return plan.getDueText();
        }

        LocalDate today = LocalDate.now();
        if (dueDate.isBefore(today)) {
            return "Overdue";
        }
        if (dueDate.isEqual(today)) {
            return "Today";
        }
        if (dueDate.isEqual(today.plusDays(1))) {
            return "Tomorrow";
        }

        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        if (!dueDate.isAfter(endOfWeek)) {
            return "This Week";
        }
        return dueDate.toString();
    }

    private LearningPlanVO toVO(LearningPlan plan) {
        LearningPlanVO vo = new LearningPlanVO();
        vo.setId(plan.getId());
        vo.setTitle(plan.getTitle());
        vo.setStudyTime(plan.getStudyTime());
        vo.setPriority(plan.getPriority());
        vo.setCompleted(plan.getCompleted());
        vo.setDueText(calculateDueText(plan));
        vo.setSubtitle(Boolean.TRUE.equals(plan.getAiGenerated()) ? "AI Generated" : "Manual Entry");
        vo.setNote(plan.getNote());
        vo.setAiGenerated(plan.getAiGenerated());
        return vo;
    }
}
