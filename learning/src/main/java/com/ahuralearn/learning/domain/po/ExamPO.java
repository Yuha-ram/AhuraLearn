package com.ahuralearn.learning.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam")
public class ExamPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String courseName;

    private Integer score;

    private Integer totalScore;

    private String status;

    private String icon;
}