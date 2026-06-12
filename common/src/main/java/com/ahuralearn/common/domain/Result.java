package com.ahuralearn.common.domain;

import com.ahuralearn.common.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "General response object")
public class Result<T> {
    @Schema(description = "Business Status Code 200-success other-fail")
    private Integer code;
    @Schema(description = "Response Message", example = "success")
    private String msg;
    @Schema(description = "Response Data")
    private T data;

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getValue(),
                ResultCode.SUCCESS.getDesc(),
                data
        );
    }

    // by enum
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(
                resultCode.getValue(),
                resultCode.getDesc(),
                null
        );
    }

    // custom
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(
                code,
                message,
                null
        );
    }

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
