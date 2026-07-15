package com.ahuralearn.game.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_lesson")
public class LearningLessonPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long userId;

    private Integer status;

    private Integer learnedSections;

    private Long latestSectionId;

    private LocalDateTime latestLearnTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}