package com.binair.admin.config;

import com.binair.admin.interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 — 注册 JWT Token 拦截器
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册 JWT Token 拦截器");
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/api/**")                   // 拦截所有 /api/** 请求
                .excludePathPatterns(                         // 放行路径
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/flights/dynamic",
                        "/api/flights/all",
                        "/api/flights/statistics/**",
                        "/api/public/**"
                );
    }
}
