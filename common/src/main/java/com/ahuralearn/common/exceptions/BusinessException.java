package com.ahuralearn.common.exceptions;

import com.ahuralearn.common.enums.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getDesc());
        this.code = resultCode.getValue();
        this.message = resultCode.getDesc();
    }

    public BusinessException(String message) {
        super(message);
        this.code = -1;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
