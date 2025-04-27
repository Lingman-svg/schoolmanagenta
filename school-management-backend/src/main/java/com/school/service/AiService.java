/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:06:20
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 21:51:59
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\AiService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;

    @Autowired
    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
        this.chatClient = chatClientBuilder
                .defaultFunctions("getSubjectInfoByNameFunction", "getStudentsByClassNameFunction", "getTeacherInfoByNameFunction", "getCourseScheduleByClassAndDayFunction", "getStudentGradeFunction")
                .build();
    }

    /**
     * Chat function that can potentially call registered functions.
     * @param userMessage User's message
     * @return AI's final response (after potential function calls)
     */
    public String chatWithFunctions(String userMessage) {
        log.info("Processing chat with functions for message: {}", userMessage);
        try {
            String content = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();

            log.info("AI response: {}", content);
            return content;

        } catch (Exception e) {
            log.error("Error during chat with functions: {}", e.getMessage(), e);
            throw new RuntimeException("Error interacting with AI service: " + e.getMessage(), e);
        }
    }

    public String simpleChat(String userMessage) {
        ChatClient simpleClient = this.chatClientBuilder.build();
        log.info("Processing simple chat for message: {}", userMessage);
        try {
            String content = simpleClient.prompt()
                 .user(userMessage)
                 .call()
                 .content();
            log.info("AI simple response: {}", content);
            return content;
         } catch (Exception e) {
             log.error("Error during simple chat: {}", e.getMessage(), e);
             throw new RuntimeException("Error interacting with AI service: " + e.getMessage(), e);
         }
    }

    // 后续可以添加更复杂的方法，例如:
    // - 使用不同的模型
    // - 传递系统消息 (System Prompt)
    // - 使用流式响应 (Streaming)
    // - 使用函数调用 (Function Calling) 来解析指令并调用业务 Service
} 