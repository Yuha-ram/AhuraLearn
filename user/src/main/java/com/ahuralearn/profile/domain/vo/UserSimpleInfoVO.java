package com.ahuralearn.profile.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Basic info for the top nav (username + email + avatar). Mirrors the contract
 * of the boss's auth/user module ({@code GET /user/simpleInfo}) so the frontend
 * works unchanged. {@code enrolledCourses} is part of that contract but the
 * course/learning domain is the teammates' part and not present here, so it is
 * returned as 0 until the real auth/user module is integrated.
 *
 * @author Dariush
 * @since 2026-06-29
 */
@Data
@AllArgsConstructor
@Schema(description = "User simple info (top nav)")
public class UserSimpleInfoVO {

    private String username;

    private String email;

    private String avatar;

    private int enrolledCourses;
}
