package com.ahuralearn.course.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseStatus implements BaseEnum {
    DRAFT(0, "uploading"),
    PUBLISHED(1, "published"),
    UNPUBLISHED(1, "removed"),
    ;

    @EnumValue
    Integer value;
    String desc;
}
