package com.ahuralearn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode implements BaseEnum {
    // Common
    SUCCESS(200, "success"),
    PARAM_ERROR(400, "Invalid request parameters"),
    UNAUTHORIZED(401, "Token is missing"),
    FORBIDDEN(403, "Insufficient permissions to access this resource"),
    NOT_FOUND(404, "The requested resource does not exist"),
    METHOD_NOT_ALLOWED(405, "Incorrect HTTP request method"),
    SYSTEM_ERROR(500, "Internal Server Error"),

    // JWT
    TOKEN_EXPIRED(4011, "Token has expired"),
    TOKEN_INVALID(4012, "Token is invalid or malformed"),
    ;

    // Business

    private final Integer value;
    private final String desc;
}
