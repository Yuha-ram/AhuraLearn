package com.ahuralearn.common.exceptions;

import com.ahuralearn.common.enums.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getDesc());
        this.resultCode = resultCode;
    }
}
