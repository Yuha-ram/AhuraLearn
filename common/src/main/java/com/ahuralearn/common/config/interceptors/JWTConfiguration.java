package com.ahuralearn.common.config.interceptors;

import com.ahuralearn.common.interceptors.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JWTConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/auth/refresh",
                        "/auth/users/exists",

                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}
