package com.ahuralearn.course.domain.po;

import java.math.BigDecimal;
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
 * Instructor table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("instructor")
@Schema(description = "Instructor Entity")
public class Instructor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Instructor name
     */
    private String name;

    /**
     * Cloud URL for instructor profile picture
     */
    private String avatar;

    /**
     * Short introduction
     */
    private String bio;

    /**
     * Average instructor rating across all courses
     */
    private BigDecimal rating;

    /**
     * Total number of student taught
     */
    private Integer studentCount;

    /**
     * Total number of courses published
     */
    private Integer courseCount;

    /**
     * Record creation time
     */
    private LocalDateTime createTime;

    /**
     * Record last update time
     */
    private LocalDateTime updateTime;


}
