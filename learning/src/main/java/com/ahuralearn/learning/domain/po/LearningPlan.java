package com.ahuralearn.learning.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("learning_plan")
public class LearningPlan {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String studyTime;

    private String priority;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean completed;

    private String dueText;

    private LocalDate dueDate;

    private String subtitle;

    private String note;

    private Boolean aiGenerated;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
