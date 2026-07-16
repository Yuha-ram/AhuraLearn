package com.ahuralearn.assistant.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * AI tutor chat message table
 * </p>
 * One message of the tutor conversation, mapped to the {@code assistant_chat_message} table.
 * Both sides are stored ({@code role} is "user" or "assistant"), scoped to the
 * owning user and the document the conversation is about. The recent history is
 * replayed into the model prompt so the tutor remembers the conversation, and it
 * is reloaded by the UI so a conversation survives a page refresh.
 *
 * @author Dariush
 * @since 2026-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("assistant_chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key — snowflake id assigned by MyBatis-Plus (ASSIGN_ID)
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * id of the user who owns this conversation — stamped from the JWT (UserContext)
     */
    private Long userId;

    /**
     * id of the document the conversation is about (null = no document selected)
     */
    private Long documentId;

    /**
     * message author: "user" or "assistant"
     */
    private String role;

    /**
     * the message text
     */
    private String content;

    /**
     * create time — filled by the DB default (CURRENT_TIMESTAMP)
     */
    private LocalDateTime createTime;

}
