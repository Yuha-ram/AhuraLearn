package com.ahuralearn.user.domain.po;

import com.ahuralearn.user.enums.UserRole;
import com.ahuralearn.user.enums.UserStatus;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * user table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(description = "User Entity")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * name
     */
    private String username;

    /**
     * pwd
     */
    private String password;

    /**
     * email address
     */
    private String email;

    /**
     * user role (e.g., 0=admin, 1=student)
     */
    private UserRole role;

    /**
     * user avatar (URL address)
     */
    private String avatar;

    /**
     * biography
     */
    private String bio;

    /**
     * account status (1=normal, 0=disabled)
     */
    private UserStatus status;

    /**
     * last login time
     */
    private LocalDateTime lastLoginTime;

    /**
     * create time
     */
    private LocalDateTime createTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;


}
