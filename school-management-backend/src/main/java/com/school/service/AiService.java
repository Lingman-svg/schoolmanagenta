/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:06:20
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:43:35
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\AiService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

/**
 * AI 服务接口，用于处理与大模型的交互
 */
public interface AiService {

    /**
     * 发送消息给 AI 模型并获取响应
     *
     * @param message 用户发送的消息
     * @return AI 模型的响应内容
     */
    String chat(String message);

} 