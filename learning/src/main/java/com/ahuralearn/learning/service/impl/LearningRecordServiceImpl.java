package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.learning.domain.po.LearningRecord;
import com.ahuralearn.learning.mapper.LearningRecordMapper;
import com.ahuralearn.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Section learning record table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-17
 */
@Service
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    @Override
    public Set<Long> getCompletedSectionIds(Long lessonId) {
        Set<Long> completedSecIds = lambdaQuery()
                .select(LearningRecord::getSectionId)
                .eq(LearningRecord::getLessonId, lessonId)
                .eq(LearningRecord::getFinished, true)
                .list()
                .stream().map(LearningRecord::getSectionId)
                .collect(Collectors.toSet());

        if (CollUtils.isEmpty(completedSecIds)) // User just enrolled, still not finish any sec
            completedSecIds = CollUtils.emptySet();
        return completedSecIds;
    }

    @Override
    public Integer getSectionMoment(Long lessonId, Long sectionId) {
        LearningRecord currentSec = lambdaQuery()
                .select(LearningRecord::getMoment)
                .eq(LearningRecord::getLessonId, lessonId)
                .eq(LearningRecord::getSectionId, sectionId)
                .one();
        return currentSec != null ? currentSec.getMoment() : 0;
    }
}
