package com.ahuralearn.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * User quiz answer records table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post_class_quiz_answer")
public class PostClassQuizAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key, auto-incremented
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * ID of the user who answered
     */
    private Long userId;

    /**
     * Redundant field for fast check of section attempt status
     */
    private Long sectionId;

    /**
     * ID of the specific question answered
     */
    private Long questionId;

    /**
     * The answer submitted by the user
     */
    private String userAnswer;

    /**
     * Grading result: 0-Incorrect, 1-Correct
     */
    private Boolean isCorrect;

    /**
     * Actual score earned (e.g., 0 or 10) to solidify historical data
     */
    private Integer earnedScore;

    /**
     * Submission time
     */
    private LocalDateTime createTime;

    /**
     * Record last update time
     */
    private LocalDateTime updateTime;


}
