package com.school.security.filter;

import com.school.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 登录授权过滤器
 */
@Slf4j
@Component // 将其声明为 Spring Bean
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(this.tokenHeader);
        // 检查 Header 是否存在且以 Bearer 开头
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            final String authToken = authHeader.substring(this.tokenHead.length()); // "Bearer " 后面的部分
            String username = null;
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
                log.debug("Checking authentication for user: {}", username);
            } catch (Exception e) {
                log.warn("JWT Token validation error: {}", e.getMessage());
                // 可以根据异常类型细化处理，例如 TokenExpiredException
            }

            // 当 Token 中的 username 不为空，且当前 SecurityContext 中没有认证信息时
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载 UserDetails
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 验证 token 是否有效
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    log.debug("Authenticated user: {}, setting security context", username);
                    // 创建 Authentication 对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // 设置请求详情
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将认证信息设置到 SecurityContext 中
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("JWT Token validation failed for user: {}", username);
                }
            }
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
} 