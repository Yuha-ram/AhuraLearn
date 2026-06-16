package com.ahuralearn.learning.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LearningStatus implements BaseEnum {
    InProgress(1,"In Progress"),
    COMPLETED(2,"Completed"),
    ;

    @EnumValue
    Integer value;
    @JsonValue
    String desc;
}
