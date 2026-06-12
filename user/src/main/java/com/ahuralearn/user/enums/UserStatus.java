package com.ahuralearn.user.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus implements BaseEnum {
    DISABLED(0, "disabled"),
    NORMAL(1, "normal"),
    ;

    @EnumValue
    Integer value;
    String desc;
}
