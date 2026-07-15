package com.ahuralearn.game.mapper;

import com.ahuralearn.game.domain.po.GamePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper extends BaseMapper<GamePO> {
}