package com.ahuralearn.game.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("game")
public class GamePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String gameCode;

    private String title;

    private String icon;

    private String description;

    private String controls;

    private String goal;

    private String tips;

}