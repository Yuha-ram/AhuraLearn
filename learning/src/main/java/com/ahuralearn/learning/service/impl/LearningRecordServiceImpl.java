package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.course.domain.po.CourseSection;
import com.ahuralearn.course.service.ICourseSectionService;
import com.ahuralearn.learning.domain.dto.LearningRecordFormDTO;
import com.ahuralearn.learning.domain.po.LearningLesson;
import com.ahuralearn.learning.domain.po.LearningRecord;
import com.ahuralearn.learning.enums.LearningStatus;
import com.ahuralearn.learning.mapper.LearningLessonMapper;
import com.ahuralearn.learning.mapper.LearningRecordMapper;
import com.ahuralearn.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    private final LearningLessonMapper lessonMapper;
    private final ICourseSectionService sectionService;

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

    @Override
    @Transactional
    public Boolean addLearningRecord(LearningRecordFormDTO formDTO) {
        // 1.params validation
        if (formDTO == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        Long userId = UserContext.getUser();
        Long courseId = formDTO.getCourseId();
        Long sectionId = formDTO.getSectionId();

        // 2.verify whether the user enrolled in the course
        LearningLesson lesson = validateEnrollment(userId, courseId);

        // 3.verify whether the section belongs to the course
        CourseSection section = validateSection(sectionId, courseId);

        Integer duration = section.getDuration();
        // 4.process learning record
        boolean firstFinish = handleVideoRecord(userId, lesson.getId(), duration, formDTO);
        // 5.update learning lesson
        handleLearningLesson(firstFinish, lesson, formDTO);

        return firstFinish;
    }

    private CourseSection validateSection(Long sectionId, Long courseId) {
        CourseSection section = sectionService.getById(sectionId);
        if (section == null || !section.getCourseId().equals(courseId))
            throw new BusinessException("Invalid section access");
        return section;
    }

    private LearningLesson validateEnrollment(Long userId, Long courseId) {
        LambdaQueryWrapper<LearningLesson> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId);
        LearningLesson lesson = lessonMapper.selectOne(wrapper);
        if (lesson == null)
            throw new BusinessException("You haven't enrolled this course");
        return lesson;
    }

    // handle with learning record
    private boolean handleVideoRecord(Long userId, Long lessonId, Integer duration, LearningRecordFormDTO formDTO) {
        Long sectionId = formDTO.getSectionId();
        Integer moment = formDTO.getMoment();

        LearningRecord old = lambdaQuery()
                .eq(LearningRecord::getLessonId, lessonId)
                .eq(LearningRecord::getSectionId, sectionId)
                .eq(LearningRecord::getUserId, userId)
                .one();
        if (old == null) { //first record
            LearningRecord newRecord = new LearningRecord();
            newRecord
                    .setUserId(userId)
                    .setLessonId(lessonId)
                    .setSectionId(sectionId)
                    .setMoment(moment)
                    .setFinished(false);
            save(newRecord);
            return false;
        }

        boolean firstFinish = !old.getFinished() && moment >= duration * 0.7; //first complete
        lambdaUpdate()
                .set(LearningRecord::getMoment, moment)
                .set(firstFinish, LearningRecord::getFinished, true)
                .set(firstFinish, LearningRecord::getFinishTime, LocalDateTime.now())
                .eq(LearningRecord::getId, old.getId())
                .update();
        return firstFinish;
    }

    private void handleLearningLesson(boolean finished, LearningLesson lesson, LearningRecordFormDTO formDTO) {
        LambdaUpdateWrapper<LearningLesson> lessonWrapper = new LambdaUpdateWrapper<>();
        lessonWrapper
                .eq(LearningLesson::getId, lesson.getId())
                .set(LearningLesson::getLatestSectionId, formDTO.getSectionId())
                .set(LearningLesson::getLatestLearnTime, LocalDateTime.now());
        if (finished) {
            int total = sectionService.lambdaQuery().eq(CourseSection::getCourseId, formDTO.getCourseId()).count().intValue();
            Integer curr = lesson.getLearnedSections();
            boolean isAllFinished = (curr + 1) >= total;
            lessonWrapper
                    .setSql("learned_sections = learned_sections + 1")
                    .set(isAllFinished, LearningLesson::getStatus, LearningStatus.COMPLETED);
        }
        lessonMapper.update(null, lessonWrapper);
    }
}
