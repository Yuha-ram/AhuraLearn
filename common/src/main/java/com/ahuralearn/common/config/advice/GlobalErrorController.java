package com.ahuralearn.common.config.advice;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.enums.ResultCode;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public Result<Void> handleError(HttpServletRequest request) {
        // get the real HTTP status code
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode != null) {
            return switch (statusCode) {
                case 404 -> Result.error(ResultCode.NOT_FOUND);
                case 401 -> Result.error(ResultCode.UNAUTHORIZED);
                case 403 -> Result.error(ResultCode.FORBIDDEN);
                case 405 -> Result.error(ResultCode.METHOD_NOT_ALLOWED);
                default -> {
                    if (statusCode >= 400 && statusCode < 500) {
                        yield Result.error(ResultCode.PARAM_ERROR);
                    } else {
                        yield Result.error(ResultCode.SYSTEM_ERROR);
                    }
                }
            };
        }

        return Result.error(ResultCode.SYSTEM_ERROR);
    }
}
