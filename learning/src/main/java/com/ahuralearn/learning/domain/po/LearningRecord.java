package com.ahuralearn.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Section learning record table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("learning_record")
public class LearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Record ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Associated lesson ID
     */
    private Long lessonId;

    /**
     * Associated section ID
     */
    private Long sectionId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Playback progress in seconds
     */
    private Integer moment;

    /**
     * Completion status: 0-Incomplete, 1-Completed
     */
    private Boolean finished;

    /**
     * Initial watch time
     */
    private LocalDateTime createTime;

    /**
     * Completion time
     */
    private LocalDateTime finishTime;

    /**
     * Last watch time (Auto-updated)
     */
    private LocalDateTime updateTime;


}
