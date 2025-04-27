package com.school.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails; // 需要 UserDetails
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // 使用 javax.crypto.SecretKey
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

/**
 * JWT Token 工具类
 */
@Component
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = Claims.SUBJECT; // 使用标准 Subject 字段存储用户名
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // 使用 HMAC-SHA 算法，需要足够长度的密钥
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从 token 中获取 JWT 中的负载 (claims)
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // 替换 deprecated 方法
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 中获取指定 claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从 token 中获取登录用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从 token 中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 判断 token 是否已经失效
     */
    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    /**
     * 根据用户信息生成 token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        // TODO: 可以根据需要添加其他信息到 claims，如用户ID、角色等
        // claims.put("roles", userDetails.getAuthorities().stream()...);
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * 生成 token 的具体逻辑
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = (Date) claims.get(CLAIM_KEY_CREATED);
        final Date expirationDate = new Date(createdDate.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(createdDate)
                .expiration(expirationDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512) // 使用推荐的 SignatureAlgorithm
                .compact();
    }

    /**
     * 验证 token 是否有效
     *
     * @param token       客户端传入的 token
     * @param userDetails 从数据库中查询出的用户信息
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 判断 token 是否可以被刷新
     * (如果实现了刷新 token 机制)
     */
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新 token
     * (如果实现了刷新 token 机制)
     */
    public String refreshToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return doGenerateToken(claims, getUsernameFromToken(token));
    }
} 