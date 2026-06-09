package com.ahuralearn.common.interceptors;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.JWTUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
//        log.info("JWT拦截器拦截到了请求路径: " + request.getRequestURI());
        String token = request.getHeader("accessToken");
        if(StringUtils.isEmpty(token))
            throw new BusinessException(ResultCode.UNAUTHORIZED);

        try {
            DecodedJWT decodedJWT = JWTUtils.verifyToken(token);
            // save the userId to threadlocal
            Long userId = decodedJWT.getClaim("userId").asLong();
            UserContext.setUser(userId);
            return true;
        } catch (TokenExpiredException e) {
            log.warn("JWT verification failed: Token expired. {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureVerificationException | AlgorithmMismatchException | JWTDecodeException e) {
            // usually hacker
            log.warn("JWT verification failed: Invalid signature or algorithm mismatch. {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        } catch (Exception e) {
            log.warn("JWT verification failed: Unexpected error. {}", e.getMessage());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
