/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:41:35
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:03:01
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\config\SecurityConfig.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.school.security.filter.JwtAuthenticationTokenFilter;
// import org.springframework.security.authentication.AuthenticationManager; // 可能需要用于密码模式
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 用于获取 AuthenticationManager

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    /**
     * 配置密码编码器 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 暂时禁用 CSRF，如果后续使用 JWT 等 stateless 认证，通常需要禁用
            .csrf(AbstractHttpConfigurer::disable)
            // 配置请求授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许登录接口匿名访问
                .requestMatchers("/login").permitAll()
                // 允许 Swagger 文档接口匿名访问 (如果使用了)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 可以禁用默认的 httpBasic 和 formLogin (如果需要)
            // .httpBasic(AbstractHttpConfigurer::disable)
            // .formLogin(AbstractHttpConfigurer::disable)
            ;
            // TODO: 后续需要添加 JWT 过滤器来处理 Token 认证 - 已完成
            // 添加 JWT filter 到过滤器链中，在 UsernamePasswordAuthenticationFilter 之前
            http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* // 如果需要显式配置 AuthenticationManager Bean (例如，用于密码模式)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    */

     // TODO: 定义 JWT 过滤器 Bean - 已完成
     // 将过滤器声明为 Bean
     @Bean
     public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
         return new JwtAuthenticationTokenFilter();
     }
} 