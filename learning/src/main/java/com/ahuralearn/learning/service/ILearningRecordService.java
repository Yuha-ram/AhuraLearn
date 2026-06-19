package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.LearningRecordFormDTO;
import com.ahuralearn.learning.domain.po.LearningRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * Section learning record table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-17
 */
public interface ILearningRecordService extends IService<LearningRecord> {

    Set<Long> getCompletedSectionIds(Long lessonId);

    Integer getSectionMoment(Long lessonId, Long sectionId);

    void addLearningRecord(LearningRecordFormDTO formDTO);
}
