package com.school.ai;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多轮对话上下文管理器，按用户维护未完成的意图解析会话
 */
@Component
public class IntentSessionManager {
    // 用于存储每个用户的未完成会话（可用用户ID或会话ID做key）
    private final Map<String, IntentParseResult> sessionMap = new ConcurrentHashMap<>();

    /**
     * 获取用户当前会话
     */
    public IntentParseResult getSession(String userId) {
        return sessionMap.get(userId);
    }

    /**
     * 保存/更新用户当前会话
     */
    public void saveSession(String userId, IntentParseResult result) {
        sessionMap.put(userId, result);
    }

    /**
     * 清除用户当前会话
     */
    public void clearSession(String userId) {
        sessionMap.remove(userId);
    }
} 