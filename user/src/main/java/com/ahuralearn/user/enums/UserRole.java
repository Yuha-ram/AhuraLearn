package com.ahuralearn.user.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements BaseEnum {
    ADMIN(0, "admin"),
    STUDENT(1, "student"),
    ;

    @EnumValue
    Integer value;
    String desc;
}
