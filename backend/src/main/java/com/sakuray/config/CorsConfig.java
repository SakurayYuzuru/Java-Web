package com.sakuray.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) 配置。
 * 允许前端在不同端口（例如 Vue 运行的 5173）访问后端（8080）。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 匹配所有 /api/ 开头的请求
                .allowedOrigins("http://localhost:5173") // 允许前端运行的端口访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许发送 Cookie 和认证信息
                .maxAge(3600); // 预检请求的缓存时间
    }
}