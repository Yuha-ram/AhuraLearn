package com.ahuralearn.adaptiveexam.mapper;

import com.ahuralearn.adaptiveexam.domain.po.AssessmentAnswerDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 答题详情 Mapper
 *
 * 原本 insertDetail 方法在 AssessmentMapper 中，
 * 迁移 MP 后拆分为独立 Mapper，使用 BaseMapper.insert() 插入。
 */
@Mapper
public interface AssessmentAnswerDetailMapper extends BaseMapper<AssessmentAnswerDetail> {
}
