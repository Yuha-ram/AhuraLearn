package com.ahuralearn.report.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("report")
public class ReportPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Integer score;

    private String level;

    private String description;

    private String message;

    private String errorsJson;

    private String knowledgeGapJson;

    private String keywordText;

    private String topic;

    private String suggestionText;
}