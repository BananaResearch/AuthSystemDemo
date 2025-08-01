package com.example.authsystem.config;

import com.example.authsystem.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册日志拦截器，拦截所有请求
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns("/**")
                // 排除静态资源请求
                .excludePathPatterns("/static/**");
    }
}