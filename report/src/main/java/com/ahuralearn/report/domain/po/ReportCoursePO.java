package com.ahuralearn.report.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class ReportCoursePO {

    private Long id;

    private String name;

    private Integer status;
}