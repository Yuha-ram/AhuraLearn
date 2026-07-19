package com.ahuralearn.adaptiveexam.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("course")
public class CourseModule {

    // =========================================================
    // 主键
    //
    // course 表的 id 是 bigint AUTO_INCREMENT（其他团队负责）
    // 此模块只做 SELECT，IdType.AUTO 告诉 MP 不要生成雪花 ID
    // JDBC 会将 BIGINT 自动转换为 String
    // =========================================================

    @TableId(type = IdType.AUTO)
    private String id;

    private String name;
}