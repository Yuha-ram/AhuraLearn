package com.ahuralearn.cms.domain.po;

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
 * Homepage promotional banners
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("banner")
@Schema(description = "Banner Entity")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Banner name
     */
    private String title;

    /**
     * Cloud storage URL of the banner image
     */
    private String imageUrl;

    /**
     * Redirect link when clicked
     */
    private String targetUrl;

    /**
     * Controls online/offline status
     */
    private Boolean isActive;

    /**
     * Record creation time
     */
    private LocalDateTime createTime;

    /**
     * Record last update time
     */
    private LocalDateTime updateTime;


}
