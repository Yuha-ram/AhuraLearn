package com.ahuralearn.assistant.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * One assistant reply in the chat.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "Academic assistant reply")
public class ChatMessageVO {

    /** message role — always "assistant" for replies from this API */
    private String role;

    /** the answer text */
    private String text;

    /** id of the document this reply is about (String for JS safety) */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;
}
