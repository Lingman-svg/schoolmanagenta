/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:13:48
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 21:16:28
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\AiController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.school.service.AiService;
import com.school.utils.R; // 引入统一响应结果类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map; // 用于接收简单的 JSON 请求体

@RestController
@RequestMapping("/ai") // 定义 AI 相关接口的基础路径
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    @Autowired
    private AiService aiService;

    /**
     * 支持 Function Calling 的 AI 聊天端点
     * @param requestBody 包含用户消息的请求体, 期望格式: {"message": "你的问题"}
     * @return AI 的最终响应 (可能经过函数调用)
     */
    @PostMapping("/chat")
    public R<String> chatWithFunctionCalling(@RequestBody Map<String, String> requestBody) {
        String userMessage = requestBody.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return R.fail("消息内容不能为空");
        }

        log.info("Received AI chat request: {}", userMessage);
        try {
            // Call the service method that handles function calling
            String aiResponse = aiService.chatWithFunctions(userMessage);
            return R.success(aiResponse);
        } catch (Exception e) {
            log.error("Error processing AI chat request: {}", e.getMessage(), e);
            // 捕获 AI 服务调用中可能出现的异常
            return R.fail("与 AI 服务交互时出错: " + e.getMessage());
        }
    }

    // 可以保留或移除 simpleChat 端点，或者给它一个不同的路径
    // @PostMapping("/simple-chat")
    // public R<String> simpleChat(@RequestBody Map<String, String> requestBody) { ... }

} 