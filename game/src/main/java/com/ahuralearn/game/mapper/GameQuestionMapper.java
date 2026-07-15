package com.ahuralearn.game.mapper;

import com.ahuralearn.game.domain.po.GameQuestionPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameQuestionMapper extends BaseMapper<GameQuestionPO> {
}