package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.LearningPlanQueryDTO;
import com.ahuralearn.learning.domain.dto.LearningPlanSaveDTO;
import com.ahuralearn.learning.domain.po.LearningPlan;
import com.ahuralearn.learning.domain.vo.LearningPlanPageVO;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LearningPlanService extends IService<LearningPlan> {
    /**
     * Create a learning plan for the current user.
     *
     * @author GXC
     */
    LearningPlanVO createPlan(LearningPlanSaveDTO saveDTO);

    LearningPlanVO createAiPlan(LearningPlanSaveDTO saveDTO);

    /**
     * Update a learning plan owned by the current user.
     *
     * @author GXC
     */
    LearningPlanVO updatePlan(Long id, LearningPlanSaveDTO saveDTO);

    LearningPlanPageVO getLearningPlanPage(Long userId, LearningPlanQueryDTO queryDTO);

    LearningPlanVO toggleComplete(Long userId, Long id);

    boolean deleteLearningPlan(Long userId, Long id);
}
