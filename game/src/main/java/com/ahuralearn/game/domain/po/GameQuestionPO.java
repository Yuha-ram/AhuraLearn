package com.ahuralearn.game.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("game_question")
public class GameQuestionPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String gameCode;

    private String questionType;

    private String questionData;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}