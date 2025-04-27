package com.school.service.impl;

import com.school.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI 服务实现类
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private final ChatClient chatClient;

    @Autowired
    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        // 注入 ChatClient.Builder，并配置函数回调
        // 这样 ChatClient 就能自动发现并使用 AiFunctionConfig 中定义的函数
        this.chatClient = chatClientBuilder
                .defaultFunctions("getSubjectInfoByNameFunction",
                                  "getStudentsByClassNameFunction",
                                  "getTeacherInfoByNameFunction",
                                  "getCourseScheduleByClassAndDayFunction",
                                  "getStudentGradeFunction")
                .build();
    }

    @Override
    public String chat(String message) {
        log.info("Sending message to AI: {}", message);
        try {
            // 构建 Prompt 对象
            Prompt prompt = new Prompt(message);

            // 调用 ChatClient 发送请求并获取响应
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

            // 从响应中提取 AI 生成的内容
            String aiContent = response.getResult().getOutput().getContent();
            log.info("Received AI response: {}", aiContent);
            return aiContent;
        } catch (Exception e) {
            log.error("Error communicating with AI service", e);
            // 返回一个友好的错误信息给用户
            return "抱歉，AI 服务暂时无法响应，请稍后再试。错误信息: " + e.getMessage();
        }
    }
} 