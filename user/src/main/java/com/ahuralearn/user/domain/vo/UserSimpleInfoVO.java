package com.ahuralearn.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "User Simple Info Entity")
public class UserSimpleInfoVO {

    private String username;

    private String email;

    private String avatar;

    private int enrolledCourses;
}
