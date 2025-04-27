/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-05-10 17:02:22
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-05-10 17:06:49
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\ai\IntentParser.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.ai;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AI自然语言指令解析器，负责调用大模型API并返回意图+参数
 */
@Component
public class IntentParser {
    /**
     * 解析用户输入的自然语言指令，返回意图和参数（此处为占位，后续可接入大模型API）
     * @param userInput 用户输入的自然语言指令
     * @return IntentParseResult
     */
    public IntentParseResult parse(String userInput) {
        // TODO: 实际实现应调用大模型API，解析意图和参数
        // 这里只做结构示例，后续可用 LLM API 替换
        IntentParseResult result = new IntentParseResult();
        result.setOriginalText(userInput);
        // 示例：简单规则匹配
        if (userInput.contains("成绩")) {
            result.setIntentType(IntentType.QUERY_STUDENT_GRADE);
            Map<String, Object> params = new HashMap<>();
            Set<String> missing = new HashSet<>();
            // 假设只要包含"张三"就认为有学生名
            if (userInput.contains("张三")) {
                params.put("studentName", "张三");
            } else {
                missing.add("studentName");
            }
            // 假设只要包含"数学"就认为有科目
            if (userInput.contains("数学")) {
                params.put("subjectName", "数学");
            } else {
                missing.add("subjectName");
            }
            result.setParams(params);
            result.setMissingParams(missing);
            result.setNeedMoreInfo(!missing.isEmpty());
        } else {
            // 其他意图类型可继续扩展
            result.setIntentType(null);
            result.setParams(new HashMap<>());
            result.setMissingParams(new HashSet<>());
            result.setNeedMoreInfo(true);
        }
        return result;
    }
} 