package com.school.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有域名进行跨域调用 (开发环境常用，生产环境应配置具体域名)
        config.addAllowedOriginPattern("*"); // Spring Boot 2.4+ 推荐使用 allowedOriginPatterns
        // config.addAllowedOrigin("http://localhost:5173"); // 或者指定具体的前端地址

        // 允许跨越发送cookie
        config.setAllowCredentials(true);

        // 允许所有请求方法跨域调用
        config.addAllowedMethod("*");

        // 放行全部原始头信息
        config.addAllowedHeader("*");

        // 允许暴露的头部信息 (例如 Content-Disposition 用于文件下载)
        config.addExposedHeader("Content-Disposition");

        // 添加映射路径，拦截一切请求
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
} 