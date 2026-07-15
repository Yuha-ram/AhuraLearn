package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.po.ExamPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamMapper extends BaseMapper<ExamPO> {
}