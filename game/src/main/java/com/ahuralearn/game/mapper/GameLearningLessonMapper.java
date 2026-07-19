package com.ahuralearn.game.mapper;

import com.ahuralearn.game.domain.po.LearningLessonPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameLearningLessonMapper extends BaseMapper<LearningLessonPO> {
}