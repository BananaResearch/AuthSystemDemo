package com.example.authsystem.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();

        logger.info("[Request] Time: {}, IP: {}, Method: {}, URL: {}", timestamp, ip, method, url);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String timestamp = LocalDateTime.now().format(formatter);
        int status = response.getStatus();
        String url = request.getRequestURL().toString();

        logger.info("[Response] Time: {}, URL: {}, Status: {}", timestamp, url, status);
    }
}