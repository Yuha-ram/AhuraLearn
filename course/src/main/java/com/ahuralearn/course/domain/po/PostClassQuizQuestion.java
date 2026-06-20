package com.ahuralearn.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Map;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Post-class quiz questions table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "post_class_quiz_question", autoResultMap = true)
public class PostClassQuizQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key, auto-incremented
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Belongs to which section
     */
    private Long sectionId;

    /**
     * Question text content
     */
    private String content;

    /**
     * Options in JSON format, e.g., {"A":"Option1", "B":"Option2"}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> options;

    /**
     * Correct answer, e.g., A
     */
    private String correctAnswer;

    /**
     * Fixed score for this question (e.g., 10)
     */
    private Integer score;

    /**
     * Record creation time
     */
    private LocalDateTime createTime;

    /**
     * Record last update time
     */
    private LocalDateTime updateTime;


}
