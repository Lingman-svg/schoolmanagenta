package com.school.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;
import java.util.Set;

/**
 * AI指令解析结果，包含意图、参数、缺失参数等
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentParseResult {
    private IntentType intentType; // 解析出的意图类型
    private Map<String, Object> params; // 已抽取的参数
    private Set<String> missingParams; // 缺失的参数名
    private boolean needMoreInfo; // 是否需要补全参数
    private String originalText; // 原始指令文本
}
