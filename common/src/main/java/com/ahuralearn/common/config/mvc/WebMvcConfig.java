package com.ahuralearn.common.config.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许跨域访问的路径（所有接口）
                .allowedOriginPatterns("*") // 允许的来源，开发阶段可以直接写 "*" 或者 "http://localhost:3000"
                .allowedMethods("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS") // 允许的请求方法，gxc加了patch
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 是否允许携带 Cookie 等凭证信息（使用 Token 通常也需要设为 true）
                .maxAge(3600); // 预检请求（OPTIONS）的缓存时间，单位秒。避免频繁发送 OPTIONS 请求
    }
}