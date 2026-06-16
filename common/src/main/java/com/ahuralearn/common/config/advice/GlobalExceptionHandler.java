package com.ahuralearn.common.config.advice;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

// Global Exception Handler
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("404 can't find the resource：{}", e.getResourcePath());
        return Result.error(ResultCode.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Invalid parameter";
        log.warn("Parameter validation failed: {}", errorMessage);

        return Result.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public Result<Void> handleTokenExpiredException(TokenExpiredException e) {
        log.warn("JWT verification failed: Token expired. {}", e.getMessage());
        return Result.error(ResultCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler({SignatureVerificationException.class, AlgorithmMismatchException.class, JWTDecodeException.class})
    public Result<Void> handleTokenInvalidException(Exception e) {
        log.warn("JWT verification failed: Invalid signature or algorithm mismatch. {}", e.getMessage());
        return Result.error(ResultCode.TOKEN_INVALID);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Failed to deserialize request body: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class
    })
    public Result<Void> handleMissingServletRequestParameterException(Exception e) {
        String errorMsg = "Required request element is missing";
        String elementName = "unknown";

        if (e instanceof MissingServletRequestParameterException ex) {
            elementName = ex.getParameterName();
            errorMsg = "Required request parameter '" + elementName + "' is missing";
        } else if (e instanceof MissingRequestHeaderException ex) {
            elementName = ex.getHeaderName();
            errorMsg = "Required request header '" + elementName + "' is missing";
        }
        log.warn(errorMsg);
        return Result.error(ResultCode.PARAM_MISSING);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        // it's usually user behavior (clicking too fast)
        log.warn("Duplicate key exception caught: {}", e.getMessage());
        return Result.error(ResultCode.DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException e) {
        // true system/DB failure
        log.error("Severe database operation exception: ", e);
        return Result.error(ResultCode.DB_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unknown exception", e);
        return Result.error(ResultCode.SYSTEM_ERROR);
    }
}
