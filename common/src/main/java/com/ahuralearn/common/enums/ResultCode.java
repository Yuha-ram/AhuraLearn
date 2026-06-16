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
    TOKEN_TYPE_ERROR(4013, "Invalid token type"),

    // Business
    PARAM_MISSING(4111, "Missing required parameters"),

    // DB
    DUPLICATE_RESOURCE(4201, "Operation too frequent or data already exists"),
    DB_FAILED(4202, "Database operation failed"),
    ;

    private final Integer value;
    private final String desc;
}
